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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public Res reviewLimitExHandler(ReviewLimitException e) {
        return new Res("fail", e.getMessage());
    }

    @PostMapping("/events")
    public Res eventHandler(@RequestBody @Valid EventDto eventDto) {
        dataInitializer.init(eventDto); // 임시 데이터 생성 메서드
        if (eventDto.getType().equals(EventType.REVIEW)) {
            reviewHandler(eventDto);
        }
        return new Res("success");
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
        if (reviewRepository.existsByPlaceIdAndReviewerId(placeId, userId)) {
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
