package com.triple.mileage.module.review.service;

import com.triple.mileage.module.domain.Photo;
import com.triple.mileage.module.domain.Place;
import com.triple.mileage.module.domain.Review;
import com.triple.mileage.module.domain.User;
import com.triple.mileage.module.place.repository.PlaceRepository;
import com.triple.mileage.module.point.repository.PointRepository;
import com.triple.mileage.module.point.service.PointService;
import com.triple.mileage.module.review.dto.EventDto;
import com.triple.mileage.module.review.repository.PhotoRepository;
import com.triple.mileage.module.review.repository.ReviewRepository;
import com.triple.mileage.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final PhotoRepository photoRepository;
    private final PointRepository pointRepository;
    private final PointService pointService;
    private final EntityManager em;


    public UUID addReview(EventDto eventDto) {
        // 저장할 객체 조회
        User user = userRepository.findById(eventDto.getUserId()).orElseThrow();
        Place place = placeRepository.findById(eventDto.getPlaceId()).orElseThrow();
        String content = eventDto.getContent();

        List<Photo> photos = photoRepository.findAllById(eventDto.getAttachedPhotoIds());

        // 마일리지 계산
        int mileage = 0;
        if (StringUtils.hasText(content)) {
            mileage += 1;
        }
        if (firstCustomer(place.getId())) {
            mileage += 1;
        }
        if (!photos.isEmpty()) {
            mileage += 1;
        }

        Review review = saveReview(eventDto.getReviewId(), user, content, place, photos);
        if (mileage != 0) {
            pointService.saveAndGiveMileage(user, review.getId(), eventDto.getType(), eventDto.getAction(), mileage);
        }
        return review.getId();
    }


    public void modifyReview(EventDto eventDto) {
        Review review = reviewRepository.findWithPhotosById(eventDto.getReviewId());
        List<Photo> newPhotos = photoRepository.findAllById(eventDto.getAttachedPhotoIds());

        int mileage = 0;
        int before = review.getPhotos().size(); // 수정 전 사진 개수
        int after = eventDto.getAttachedPhotoIds().size(); // 수정 후 사진 개수

        resetPhoto(review);
        saveNewPhoto(review, newPhotos);

        review.modify(eventDto.getContent());

        if (addFirstPhoto(before, after)) {
            mileage += 1;
        } else if (removeAllPhoto(before, after)) {
            mileage -= 1;
        }

        if (mileage != 0) {
            pointService.saveAndGiveMileage(review.getReviewer(), review.getId(), eventDto.getType(), eventDto.getAction(), mileage);
        }
    }

    public void deleteReview(EventDto eventDto) {
        Review review = reviewRepository.findWithPhotosById(eventDto.getReviewId());
        int mileageSum = pointRepository.findSumByPlaceIdAndUserId(eventDto.getPlaceId(), eventDto.getUserId());
        if (mileageSum > 0) {
            pointService.saveAndGiveMileage(review.getReviewer(), review.getId(), eventDto.getType(), eventDto.getAction(), mileageSum * -1);
        }

        List<Photo> photos = review.getPhotos();
        for (Photo photo : photos) {
            photo.setReview(null);
        }

        reviewRepository.delete(review);
    }

    private void saveNewPhoto(Review review, List<Photo> photos) {
        for (Photo photo : photos) {
            photo.setReview(review);
        }
    }

    private Review saveReview(UUID id, User reviewer, String content, Place place, List<Photo> photos) {
        Review review = new Review(id, reviewer, content, place);
        reviewRepository.saveAndFlush(review);
        for (Photo photo : photos) {
            photo.setReview(review);
        }
        return review;
    }

    private void resetPhoto(Review review) {
        for (Photo photo : review.getPhotos()) {
            photo.setReview(null);
        }
    }

    private boolean firstCustomer(UUID placeId) {
        return !reviewRepository.existsByPlaceId(placeId);
    }


    private boolean addFirstPhoto(int before, int after) {
        return before == 0 && after > 0;
    }

    private boolean removeAllPhoto(int before, int after) {
        return before > 0 && after == 0;
    }


}
