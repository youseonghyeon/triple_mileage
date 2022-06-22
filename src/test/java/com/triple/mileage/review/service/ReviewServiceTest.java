package com.triple.mileage.review.service;

import com.triple.mileage.TestConst;
import com.triple.mileage.TestUtils;
import com.triple.mileage.domain.*;
import com.triple.mileage.point.repository.PointRepository;
import com.triple.mileage.review.dto.EventDto;
import com.triple.mileage.review.repository.ReviewRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

@Commit
@Transactional
@SpringBootTest
class ReviewServiceTest {

    @Autowired
    ReviewService reviewService;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    PointRepository pointRepository;
    @Autowired
    TestUtils testUtils;

    @Test
    @DisplayName("첫번째 리뷰 추가(사진X)")
    void addReview() {
        //given
        User user = testUtils.createUser();
        Place place = testUtils.createPlace();
        UUID reviewId = UUID.randomUUID();
        EventDto eventDto = testUtils.createEventDto(user, place, reviewId, new ArrayList<>());
        //when
        reviewService.addReview(eventDto);
        //then
        Review review = reviewRepository.findById(reviewId).orElseThrow();
        assertEquals(review.getReviewer(), user);
        assertEquals(review.getContent(), TestConst.REVIEW_CONTENT);
        assertEquals(review.getPlace(), place);
        assertEquals(review.getPhotos().size(), 0);
        assertNotNull(review.getCreatedDate());
        assertNotNull(review.getModifiedDate());
        //then2 예측 마일리지 = 첫번째 리뷰(1) + 리뷰(1) = 2점
        PointHistory history = pointRepository.findByReviewIdAndReceiverId(reviewId, user.getId());
        assertEquals(user.getMileage(), 2);
        assertEquals(history.getValue(), 2);
        assertEquals(history.getType(), EventType.valueOf(TestConst.POINT_TYPE));
        assertEquals(history.getAction(), EventAction.valueOf(TestConst.POINT_ACTION));
        assertEquals(history.getReceiver(), user);
        assertNotNull(history.getCreatedDate());
        assertNotNull(history.getModifiedDate());
        assertNull(history.getCreatedBy());
        assertNull(history.getModifiedBy());
    }

    @Test
    void modifyReview() {
    }

    @Test
    void deleteReview() {
    }
}
