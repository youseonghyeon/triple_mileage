package com.triple.mileage.review.service;

import com.triple.mileage.domain.Photo;
import com.triple.mileage.domain.Place;
import com.triple.mileage.domain.Review;
import com.triple.mileage.domain.User;
import com.triple.mileage.place.repository.PlaceRepository;
import com.triple.mileage.review.dto.EventDto;
import com.triple.mileage.review.repository.PhotoRepository;
import com.triple.mileage.review.repository.ReviewRepository;
import com.triple.mileage.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final PhotoRepository photoRepository;


    public void addReview(EventDto eventDto) {
        User reviewer = userRepository.findById(eventDto.getUserId()).orElseThrow();
        Place place = placeRepository.findById(eventDto.getPlaceId()).orElseThrow();
        Review review = new Review(eventDto.getReviewId(), eventDto.getContent(), reviewer, place);
        reviewRepository.save(review);
    }

    public void modifyReview(EventDto eventDto) {
        Review review = reviewRepository.findWithPhotosById(eventDto.getReviewId());
        int pre = review.getPhotos().size(); // 수정 전 사진 개수
        int size = eventDto.getAttachedPhotoIds().size(); // 수정 후 사진 개수
        List<Photo> photos = photoRepository.findAllById(eventDto.getAttachedPhotoIds());
        review.modify(eventDto.getContent(), photos);
    }

    public void deleteReview(EventDto eventDto) {
        UUID reviewId = eventDto.getReviewId();
        reviewRepository.deleteById(reviewId);
    }

}
