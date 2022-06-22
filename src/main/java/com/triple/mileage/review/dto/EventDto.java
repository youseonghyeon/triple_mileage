package com.triple.mileage.review.dto;

import com.triple.mileage.domain.EventAction;
import com.triple.mileage.domain.EventType;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class EventDto {

    private EventType type;
    private EventAction action;
    private UUID reviewId;
    private String content;
    private List<UUID> attachedPhotoIds;
    private UUID userId;
    private UUID placeId;
}
