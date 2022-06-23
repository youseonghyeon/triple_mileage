package com.triple.mileage.module.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Photo extends BaseTimeEntity {

    @Id
    @Column(name = "photo_id", columnDefinition = "BINARY(16)")
    private UUID id;

    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    public static Photo createPhoto(UUID photoId, String path, Review review) {
        Photo photo = new Photo();
        photo.id = photoId;
        photo.path = path;
        photo.review = review;
        return photo;
    }

    public static Photo createPhotoWithoutReview(UUID photoId, String path) {
        Photo photo = new Photo();
        photo.id = photoId;
        photo.path = path;
        return photo;
    }

    public void setReview(Review review) {
        this.review = review;
        if (review != null) {
            review.getPhotos().add(this);
        }
    }
}
