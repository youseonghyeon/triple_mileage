package com.triple.mileage.module.point.service;

import com.triple.mileage.module.domain.PointHistory;
import com.triple.mileage.module.domain.User;
import com.triple.mileage.module.point.repository.PointRepository;
import com.triple.mileage.module.review.dto.EventDto;
import com.triple.mileage.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final UserRepository userRepository;

    public void saveAndGiveMileage(User user, EventDto dto, int mileage) {
        PointHistory pointHistory = new PointHistory(user, dto.getReviewId(), dto.getType(), dto.getAction(), mileage);
        pointRepository.save(pointHistory);
        // 마일리지 부여
        user.giveMileage(mileage);
        userRepository.save(user);
    }


}
