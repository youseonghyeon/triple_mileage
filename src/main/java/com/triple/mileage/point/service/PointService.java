package com.triple.mileage.point.service;

import com.triple.mileage.domain.*;
import com.triple.mileage.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    public void saveAndGiveMileage(User user, Review review, EventType type, EventAction action, int mileage) {
        PointHistory pointHistory = new PointHistory(review.getId(), user, type, action, mileage);
        pointRepository.save(pointHistory);
        // 마일리지 부여
        User reviewer = review.getReviewer();
        reviewer.giveMileage(mileage);
    }


}
