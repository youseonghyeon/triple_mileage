package com.triple.mileage.point.service;

import com.triple.mileage.domain.*;
import com.triple.mileage.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    public void saveAndGiveMileage(Review review, EventType type, EventAction action, int mileage) {
        PointHistory history = new PointHistory(review, type, action, mileage);
        pointRepository.save(history);
        // 마일리지 부여
        User reviewer = review.getReviewer();
        reviewer.giveMileage(mileage);
    }

}
