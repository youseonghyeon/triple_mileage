package com.triple.mileage.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "review_id", columnDefinition = "BINARY(16)")
    private UUID id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User reviewer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    @OneToMany(mappedBy = "review")
    private List<Photo> photos;

    public Review(String content, User reviewer, Place place) {
        this.content = content;
        this.reviewer = reviewer;
        this.place = place;
    }

    public Review(UUID id, String content, User reviewer, Place place) {
        this.id = id;
        this.content = content;
        this.reviewer = reviewer;
        this.place = place;
    }

    public void modify(String content, List<Photo> photos) {
        this.content = content;
        this.photos = photos;
    }
}
