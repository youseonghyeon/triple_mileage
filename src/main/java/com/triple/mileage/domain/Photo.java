package com.triple.mileage.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Photo extends BaseEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "photo_id", columnDefinition = "BINARY(16)")
    private UUID id;

    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    public static Photo createPhoto(String path, Review review) {
        Photo p = new Photo();
        p.path = path;
        p.review = review;
        return p;
    }

    public static Photo createPhotoWithoutReview(String path) {
        // photo를 미리 생성하고 review에 연결을 늦게 하는 경우에 사용할 메소드
        Photo p = new Photo();
        p.path = path;
        return p;
    }
}
