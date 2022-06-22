package com.triple.mileage.point.service;

import com.triple.mileage.TestUtils;
import com.triple.mileage.domain.*;
import com.triple.mileage.point.repository.PointRepository;
import com.triple.mileage.review.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class PointServiceTest {

    @Autowired
    PointService pointService;
    @Autowired
    PointRepository pointRepository;
    @Autowired
    TestUtils testUtils;
    @Autowired
    ReviewRepository reviewRepository;

    @Test
    @DisplayName("마일리지 부여 (3점)")
    void saveAndGiveMileage() {
        //given
        User user = testUtils.createUser();
        Place place = testUtils.createPlace();
        Review review = testUtils.createReview(user, place);
        //when
        pointService.saveAndGiveMileage(user, review, EventType.REVIEW, EventAction.ADD, 3);
        //then
        List<PointHistory> historyList = pointRepository.findByReviewIdAndUserId(review.getId(), user.getId());
        PointHistory history = historyList.get(0);
        assertEquals(history.getAction(), EventAction.ADD);
        assertEquals(history.getType(), EventType.REVIEW);
        assertEquals(history.getReceiver(), user);
        assertNotNull(history.getCreatedDate());
        assertNotNull(history.getModifiedDate());
        assertEquals(user.getMileage(), 3);
    }
}
