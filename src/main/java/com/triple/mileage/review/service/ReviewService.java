package com.triple.mileage.review.service;

import com.triple.mileage.domain.*;
import com.triple.mileage.place.repository.PlaceRepository;
import com.triple.mileage.point.repository.PointRepository;
import com.triple.mileage.point.service.PointService;
import com.triple.mileage.review.dto.EventDto;
import com.triple.mileage.review.repository.PhotoRepository;
import com.triple.mileage.review.repository.ReviewRepository;
import com.triple.mileage.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
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


    public UUID addReview(EventDto eventDto) {
        // 저장할 값 조회
        User user = userRepository.findById(eventDto.getUserId()).orElseThrow();
        Place place = placeRepository.findById(eventDto.getPlaceId()).orElseThrow();
        String content = eventDto.getContent();
        List<UUID> attachedPhotoIds = eventDto.getAttachedPhotoIds();
        List<Photo> photos = new ArrayList<>();
        if (!attachedPhotoIds.isEmpty()) {
            photos.addAll(photoRepository.findAllById(attachedPhotoIds));
        }
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
            pointService.saveAndGiveMileage(user, review, eventDto.getType(), eventDto.getAction(), mileage);
        }
        return review.getId();
    }


    public UUID modifyReview(EventDto eventDto) {
        Review review = reviewRepository.findById(eventDto.getReviewId()).orElseThrow();
        List<Photo> photos = photoRepository.findAllById(eventDto.getAttachedPhotoIds());

        int before = review.getPhotos().size(); // 수정 전 사진 개수
        int after = eventDto.getAttachedPhotoIds().size(); // 수정 후 사진 개수
        review.modify(eventDto.getContent(), photos);

        int mileage = 0;
        if (addFirstPhoto(before, after)) {
            mileage += 1;
        } else if (removeAllPhoto(before, after)) {
            mileage -= 1;
        }

        if (mileage != 0) {
            pointService.saveAndGiveMileage(review.getReviewer(), review, eventDto.getType(), eventDto.getAction(), mileage);
        }
        return review.getId();
    }

    public void deleteReview(EventDto eventDto) {
        Review review = reviewRepository.findById(eventDto.getReviewId()).orElseThrow();
        int pointSum = pointRepository.findSumByPlaceIdAndUserId(eventDto.getPlaceId(), eventDto.getUserId());
        if (pointSum > 0) {
            pointService.saveAndGiveMileage(review.getReviewer(), review, eventDto.getType(), eventDto.getAction(), pointSum * -1);
        }
        reviewRepository.delete(review);
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


    private Review saveReview(UUID id, User reviewer, String content, Place place, List<Photo> photos) {
        Review review = new Review(id, reviewer, content, place, photos);
        reviewRepository.save(review);
        return review;
    }

}
