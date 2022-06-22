package com.triple.mileage.review.controller;

import com.triple.mileage.domain.EventAction;
import com.triple.mileage.domain.EventType;
import com.triple.mileage.review.dto.EventDto;
import com.triple.mileage.review.repository.ReviewRepository;
import com.triple.mileage.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/events")
    public void eventHandler(@RequestBody @Valid EventDto eventDto) {
        if (eventDto.getType().equals(EventType.REVIEW)) {
            reviewHandler(eventDto);
        }
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
            throw new RuntimeException("이미 등록된 리뷰가 존재합니다.");
        }
    }
}
