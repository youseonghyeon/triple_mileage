package com.triple.mileage.module.point.dto;

import com.triple.mileage.module.domain.EventAction;
import com.triple.mileage.module.domain.EventType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PointHistoryDto {

    private EventType type;
    private EventAction action;
    private int value;
    private LocalDateTime date;


    public PointHistoryDto(EventType type, EventAction action, int value, LocalDateTime date) {
        this.type = type;
        this.action = action;
        this.value = value;
        this.date = date;
    }

}
