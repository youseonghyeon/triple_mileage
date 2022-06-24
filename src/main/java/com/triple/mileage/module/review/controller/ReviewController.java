package com.triple.mileage.module.review.controller;

import com.triple.mileage.infra.DataInitializer;
import com.triple.mileage.infra.exception.ReviewLimitException;
import com.triple.mileage.module.domain.*;
import com.triple.mileage.module.place.repository.PlaceRepository;
import com.triple.mileage.module.review.repository.PhotoRepository;
import com.triple.mileage.module.review.repository.ReviewRepository;
import com.triple.mileage.module.review.service.ReviewService;
import com.triple.mileage.module.review.dto.EventDto;
import com.triple.mileage.module.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final ReviewService reviewService;
    private final DataInitializer dataInitializer;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final PhotoRepository photoRepository;

    @ExceptionHandler(ReviewLimitException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Res reviewLimitEkxHandler(ReviewLimitException e) {
        return new Res("fail", e.getMessage());
    }


    @PostMapping("/events")
    public ResponseEntity eventHandler(@RequestBody @Valid EventDto eventDto) {
        // 임시 데이터 생성 메서드
        dataInitializer.init(eventDto);

        if (eventDto.getType().equals(EventType.REVIEW)) {
            reviewHandler(eventDto);
        }

        return ResponseEntity.ok().body(new Res("success"));
    }

    private void reviewHandler(EventDto eventDto) {
        EventAction action = eventDto.getAction();
        if (action.equals(EventAction.ADD)) {
            reviewLimitValidator(eventDto);

            // 객체 조회
            User user = userRepository.findById(eventDto.getUserId()).orElseThrow();
            Place place = placeRepository.findById(eventDto.getPlaceId()).orElseThrow();
            List<Photo> photos = photoRepository.findAllById(eventDto.getAttachedPhotoIds());

            reviewService.addReview(user, place, photos, eventDto);

        } else if (action.equals(EventAction.MOD)) {
            // 객체 조회
            Review review = reviewRepository.findWithPhotosById(eventDto.getReviewId());
            List<Photo> newPhotos = photoRepository.findAllById(eventDto.getAttachedPhotoIds());

            reviewService.modifyReview(review, newPhotos, eventDto);

        } else if (action.equals(EventAction.DELETE)) {
            // 객체 조회
            Review review = reviewRepository.findWithPhotosById(eventDto.getReviewId());

            reviewService.deleteReview(review, eventDto);

        }
    }

    private void reviewLimitValidator(EventDto eventDto) {
        UUID reviewId = eventDto.getReviewId();
        UUID userId = eventDto.getUserId();
        UUID placeId = eventDto.getPlaceId();
        if (reviewRepository.existsById(reviewId)) {
            // reviewId 중복
            throw new ReviewLimitException("이미 등록된 리뷰가 존재합니다.");
        }
        if (reviewRepository.existsByPlaceIdAndReviewerId(placeId, userId)) {
            // 리뷰 개수 제한
            throw new ReviewLimitException("이미 등록된 리뷰가 존재합니다.");
        }
    }

    @Data
    @AllArgsConstructor
    static class Res {
        String result;
        String message;

        public Res(String result) {
            this.result = result;
        }
    }
}
