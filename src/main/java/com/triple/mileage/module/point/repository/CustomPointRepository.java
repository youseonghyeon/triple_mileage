package com.triple.mileage.module.point.repository;

import com.triple.mileage.module.domain.PointHistory;

import java.util.List;
import java.util.UUID;

public interface CustomPointRepository {

    int findSumByPlaceIdAndUserId(UUID placeId, UUID userId);

    List<PointHistory> findByReviewIdAndUserId(UUID reviewId, UUID receiverId);
}
