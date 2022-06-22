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
public class PointHistory extends BaseEntity {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User receiver;

    public PointHistory(Review review, String type, String action, int value) {
        this.review = review;
        this.receiver = review.getReviewer();
        this.type = EventType.valueOf(type);
        this.action = EventAction.valueOf(action);
        this.value = value;
    }

    public PointHistory(Review review, EventType type, EventAction action, int value) {
        this.review = review;
        this.receiver = review.getReviewer();
        this.type = type;
        this.action = action;
        this.review = review;
        this.value = value;
    }
}
