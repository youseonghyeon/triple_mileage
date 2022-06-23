package com.triple.mileage.module.review.controller;

import com.triple.mileage.infra.DataInitializer;
import com.triple.mileage.infra.exception.ReviewLimitException;
import com.triple.mileage.module.domain.EventAction;
import com.triple.mileage.module.domain.EventType;
import com.triple.mileage.module.review.repository.ReviewRepository;
import com.triple.mileage.module.review.service.ReviewService;
import com.triple.mileage.module.review.dto.EventDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final ReviewService reviewService;
    private final DataInitializer dataInitializer;

    @ExceptionHandler(ReviewLimitException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Res reviewLimitEkxHandler(ReviewLimitException e) {
        return new Res("fail", e.getMessage());
    }


    @PostMapping("/events")
    public ResponseEntity eventHandler(@RequestBody @Valid EventDto eventDto) {
        // mockData 생성 메서드
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
            reviewService.addReview(eventDto);

        } else if (action.equals(EventAction.MOD)) {
            reviewService.modifyReview(eventDto);

        } else if (action.equals(EventAction.DELETE)) {
            reviewService.deleteReview(eventDto);

        }
    }

    private void reviewLimitValidator(EventDto eventDto) {
        UUID userId = eventDto.getUserId();
        UUID placeId = eventDto.getPlaceId();
        if (reviewRepository.existsById(eventDto.getReviewId())) {
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
