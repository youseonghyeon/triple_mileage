package com.triple.mileage.module.point.dto;

import com.triple.mileage.module.domain.EventAction;
import com.triple.mileage.module.domain.EventType;
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


    public PointHistoryDto(EventType type, EventAction action, int value, UUID reviewId, LocalDateTime date) {
        this.type = type;
        this.action = action;
        this.value = value;
        this.reviewId = reviewId;
        this.date = date;
    }

}
