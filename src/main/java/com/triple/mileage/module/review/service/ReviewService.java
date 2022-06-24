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
import org.springframework.util.StringUtils;

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

        Review review = saveReview(eventDto.getReviewId(), user, eventDto.getContent(), place, photos);
        if (mileage != 0) {
            pointService.saveAndGiveMileage(user, review.getId(), eventDto.getType(), eventDto.getAction(), mileage);
        }
        return review.getId();
    }


    public void modifyReview(Review review, List<Photo> newPhotos, EventDto eventDto) {
        int mileage = mileagePolicy.modifyReviewMileage(review, eventDto);

        review.modify(eventDto.getContent(), newPhotos);

        if (mileage != 0) {
            pointService.saveAndGiveMileage(review.getReviewer(), review.getId(), eventDto.getType(), eventDto.getAction(), mileage);
        }
    }

    public void deleteReview(Review review, EventDto eventDto) {
        // photo 연관관계 해제
        review.resetPhotos();
        int mileageSum = mileagePolicy.deleteReviewMileage(eventDto.getPlaceId(), eventDto.getUserId());
        if (mileageSum > 0) {
            pointService.saveAndGiveMileage(review.getReviewer(), review.getId(), eventDto.getType(), eventDto.getAction(), mileageSum * -1);
        }

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
