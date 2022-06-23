package com.triple.mileage.module.point.service;

import com.triple.mileage.module.domain.*;
import com.triple.mileage.module.point.repository.PointRepository;
import com.triple.mileage.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final UserRepository userRepository;

    public void saveAndGiveMileage(User user, UUID reviewId, EventType type, EventAction action, int mileage) {
        // 마일리지 기록 저장
        PointHistory pointHistory = new PointHistory(reviewId, user, type, action, mileage);
        pointRepository.save(pointHistory);
        // 마일리지 부여
        user.giveMileage(mileage);
        userRepository.save(user);
    }


}
