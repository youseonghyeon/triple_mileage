package com.triple.mileage.module.point.controller;

import com.triple.mileage.module.domain.User;
import com.triple.mileage.module.point.dto.HistoryRes;
import com.triple.mileage.module.point.dto.PointHistoryDto;
import com.triple.mileage.module.point.dto.PointRes;
import com.triple.mileage.module.point.repository.PointRepository;
import com.triple.mileage.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PointController {

    private final UserRepository userRepository;
    private final PointRepository pointRepository;

    // 포인트 조회
    @GetMapping("/point/{userId}")
    public PointRes currentPoint(@PathVariable("userId") UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        int mileage = user.getMileage();
        return new PointRes(mileage);
    }

    // 포인트 내역 조회
    @GetMapping("/history/{userId}")
    public HistoryRes pointHistoryList(@PathVariable("userId") UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<PointHistoryDto> historyDto = pointRepository.findHistoryDto(userId);

        return new HistoryRes(user.getMileage(), historyDto);
    }

}
