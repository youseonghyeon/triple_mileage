package com.triple.mileage.module.point.dto;

import com.triple.mileage.module.domain.EventAction;
import com.triple.mileage.module.domain.EventType;
import com.triple.mileage.module.domain.PointHistory;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PointHistoryDto {

    private EventType type;
    private EventAction action;
    private int value;
    private UUID reviewId;
    private LocalDateTime date;

    public PointHistoryDto(PointHistory pointHistory) {
        this.type = pointHistory.getType();
        this.action = pointHistory.getAction();
        this.value = pointHistory.getValue();
        this.reviewId = pointHistory.getReviewId();
        this.date = pointHistory.getCreatedDate();
    }
}
