package com.triple.mileage.module.review.service;

import com.triple.mileage.module.domain.Photo;
import com.triple.mileage.module.domain.Place;
import com.triple.mileage.module.domain.Review;
import com.triple.mileage.module.domain.User;
import com.triple.mileage.module.point.policy.MileagePolicy;
import com.triple.mileage.module.point.service.PointService;
import com.triple.mileage.module.review.dto.EventDto;
import com.triple.mileage.module.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PointService pointService;
    private final MileagePolicy mileagePolicy;


    public UUID addReview(User user, Place place, List<Photo> photos, EventDto eventDto) {
        int mileage = mileagePolicy.addReviewMileage(eventDto);
        if (mileage != 0) {
            pointService.saveAndGiveMileage(user, eventDto.getReviewId(), eventDto.getType(), eventDto.getAction(), mileage);
        }

        Review review = saveReview(eventDto.getReviewId(), user, eventDto.getContent(), place, photos);
        return review.getId();
    }


    public void modifyReview(Review previousReview, List<Photo> newPhotos, EventDto eventDto) {
        int mileage = mileagePolicy.modifyReviewMileage(previousReview, eventDto);
        if (mileage != 0) {
            pointService.saveAndGiveMileage(previousReview.getReviewer(), previousReview.getId(), eventDto.getType(), eventDto.getAction(), mileage);
        }

        previousReview.modify(eventDto.getContent(), newPhotos);
    }

    public void deleteReview(Review review, EventDto eventDto) {
        int mileageSum = mileagePolicy.deleteReviewMileage(eventDto.getPlaceId(), eventDto.getUserId());
        if (mileageSum > 0) {
            pointService.saveAndGiveMileage(review.getReviewer(), review.getId(), eventDto.getType(), eventDto.getAction(), mileageSum * -1);
        } else if (mileageSum < 0) {
            log.warn("해당 Place에서 리뷰로 쌓은 마일리지가 ({})입니다. userId={}, placeId={}", mileageSum, eventDto.getUserId(), eventDto.getPlaceId());
        }

        // photo 연관관계 해제
        review.resetPhotos();
        reviewRepository.delete(review);
    }


    private Review saveReview(UUID id, User reviewer, String content, Place place, List<Photo> photos) {
        // *** review와 savedReview는 다른 객체임 (id(PK) 직접할당 특징)
        Review review = new Review(id, reviewer, content, place);
        Review savedReview = reviewRepository.save(review);
        for (Photo photo : photos) {
            photo.setReview(savedReview);
        }
        return savedReview;
    }


}
