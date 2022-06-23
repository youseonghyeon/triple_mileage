package com.triple.mileage.module.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id
    @Column(name = "review_id", columnDefinition = "BINARY(16)")
    private UUID id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    @OneToMany(mappedBy = "review", cascade = CascadeType.REMOVE)
    private List<Photo> photos;


    public Review(UUID id, String content, User reviewer, Place place) {
        this.id = id;
        this.content = content;
        this.reviewer = reviewer;
        this.place = place;
    }

    public Review(UUID id, User reviewer, String content, Place place, List<Photo> photos) {
        this.id = id;
        this.content = content;
        this.reviewer = reviewer;
        this.place = place;
        this.photos = photos;
    }

    public void modify(String content, List<Photo> photos) {
        this.content = content;
        this.photos = photos;
    }
}
