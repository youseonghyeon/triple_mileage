package com.triple.mileage.module.point.controller;

import com.triple.mileage.module.domain.User;
import com.triple.mileage.module.point.dto.HistoryRes;
import com.triple.mileage.module.point.dto.PointHistoryDto;
import com.triple.mileage.module.point.dto.PointRes;
import com.triple.mileage.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class PointController {

    private final UserRepository userRepository;

    @GetMapping("/point/{userId}")
    public PointRes currentPoint(@PathVariable("userId") UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        int mileage = user.getMileage();
        return new PointRes(mileage);
    }

    @GetMapping("/history/{userId}")
    public HistoryRes pointHistoryList(@PathVariable("userId") UUID userId) {
        User user = userRepository.findWithPointHistoryByIdOrderByCreatedDateAsc(userId);
        List<PointHistoryDto> dtoList = user.getPointHistories()
                .stream()
                .map(PointHistoryDto::new)
                .collect(Collectors.toList());

        return new HistoryRes(user.getMileage(),dtoList);
    }
}
