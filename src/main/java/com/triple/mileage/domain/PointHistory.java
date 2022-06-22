package com.triple.mileage.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "point_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Enumerated(value = EnumType.STRING)
    private EventType type;

    @Enumerated(value = EnumType.STRING)
    private EventAction action;

    private int value;

    @Column(columnDefinition = "BINARY(16)")
    private UUID reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User receiver;

    public PointHistory(UUID reviewId, User user, String type, String action, int value) {
        this.reviewId = reviewId;
        this.receiver = user;
        this.type = EventType.valueOf(type);
        this.action = EventAction.valueOf(action);
        this.value = value;
    }

    public PointHistory(UUID reviewId,User user, EventType type, EventAction action, int value) {
        this.reviewId = reviewId;
        this.receiver = user;
        this.type = type;
        this.action = action;
        this.value = value;
    }
}
