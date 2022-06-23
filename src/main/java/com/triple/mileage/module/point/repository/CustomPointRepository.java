package com.triple.mileage.module.point.repository;

import com.triple.mileage.module.domain.PointHistory;

import java.util.List;
import java.util.UUID;

public interface CustomPointRepository {

    List<PointHistory> findByReviewIdAndUserId(UUID reviewId, UUID receiverId);

    int findSumByPlaceIdAndUserId(UUID placeId, UUID userId);
}
