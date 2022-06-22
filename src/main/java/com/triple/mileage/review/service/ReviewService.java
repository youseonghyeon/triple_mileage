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

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final PhotoRepository photoRepository;
    private final PointService pointService;
    private final PointRepository pointRepository;


    public void addReview(EventDto eventDto) {
        User reviewer = userRepository.findById(eventDto.getUserId()).orElseThrow();
        Review review = saveReview(reviewer, eventDto);

        int mileage = 0;
        if (!eventDto.getContent().isEmpty()) {
            mileage += 1;
        }
        if (firstCustomer(eventDto)) {
            mileage += 1;
        }
        if (!eventDto.getAttachedPhotoIds().isEmpty()) {
            mileage += 1;
        }

        if (mileage != 0) {
            pointService.saveAndGiveMileage(review, eventDto, mileage);
        }
    }


    public void modifyReview(EventDto eventDto) {
        Review review = reviewRepository.findWithPhotosById(eventDto.getReviewId());
        int before = review.getPhotos().size(); // 수정 전 사진 개수
        int after = eventDto.getAttachedPhotoIds().size(); // 수정 후 사진 개수
        List<Photo> photos = photoRepository.findAllById(eventDto.getAttachedPhotoIds());

        review.modify(eventDto.getContent(), photos);

        int mileage = 0;
        if (addFirstPhoto(before, after)) {
            mileage += 1;
        } else if (removeAllPhoto(before, after)) {
            mileage -= 1;
        }

        if (mileage != 0) {
            pointService.saveAndGiveMileage(eventDto, mileage);
        }
    }

    public void deleteReview(EventDto eventDto) {
        Review review = reviewRepository.findById(eventDto.getReviewId()).orElseThrow();
        PointHistory pointHistory = pointRepository.findByReviewIdAndReceiverId(review.getId(), review.getReviewer().getId());
        if (pointHistory == null) {
            log.info("포인트 내역이 없음 reviewId={}", review.getId());
            throw new RuntimeException("포인트 내역이 존재하지 않습니다.");
        }
        int mileage = pointHistory.getValue();
        pointService.saveAndGiveMileage(review, eventDto, mileage * (-1));
        reviewRepository.delete(review);
    }


    private boolean firstCustomer(EventDto eventDto) {
        return !reviewRepository.existsByPlaceId(eventDto.getPlaceId());
    }

    private Review saveReview(User reviewer, EventDto eventDto) {
        Place place = placeRepository.findById(eventDto.getPlaceId()).orElseThrow();
        Review review = new Review(eventDto.getReviewId(), eventDto.getContent(), reviewer, place);
        reviewRepository.save(review);
        return review;
    }

    private boolean addFirstPhoto(int before, int after) {
        return before == 0 && after > 0;
    }

    private boolean removeAllPhoto(int before, int after) {
        return before > 0 && after == 0;
    }


}
