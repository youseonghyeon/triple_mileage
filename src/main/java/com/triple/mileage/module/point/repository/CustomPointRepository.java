package com.triple.mileage.module.point.repository;

import com.triple.mileage.module.point.dto.PointHistoryDto;

import java.util.List;
import java.util.UUID;

public interface CustomPointRepository {


    Integer findSumByReviewId(UUID reviewId);

    List<PointHistoryDto> findHistoryDto(UUID userId);
}
