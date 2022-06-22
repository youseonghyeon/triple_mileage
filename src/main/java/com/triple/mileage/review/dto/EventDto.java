package com.triple.mileage.review.dto;

import com.triple.mileage.domain.EventAction;
import com.triple.mileage.domain.EventType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class EventDto {

    @NotNull
    private EventType type;
    @NotNull
    private EventAction action;
    @NotNull
    private UUID reviewId;
    private String content;
    private List<UUID> attachedPhotoIds = new ArrayList<>();
    @NotNull
    private UUID userId;
    @NotNull
    private UUID placeId;
}
