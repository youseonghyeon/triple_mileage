package com.triple.mileage.point.service;

import com.triple.mileage.TestUtils;
import com.triple.mileage.domain.*;
import com.triple.mileage.point.repository.PointRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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

    @Test
    void saveAndGiveMileage() {
        User user = testUtils.createUser();
        Place place = testUtils.createPlace();
        Review review = testUtils.createReview(user, place);

        pointService.saveAndGiveMileage(review, EventType.REVIEW, EventAction.ADD, 4);
        PointHistory history = pointRepository.findById(review.getId()).orElseThrow();

        assertEquals(history.getAction(), EventAction.ADD);
        assertEquals(history.getType(), EventType.REVIEW);
        assertEquals(history.getReceiver(), user);
        assertNotNull(history.getCreatedDate());
        assertNotNull(history.getModifiedDate());
        assertNull(history.getCreatedBy());
        assertNull(history.getModifiedBy());


    }
}
