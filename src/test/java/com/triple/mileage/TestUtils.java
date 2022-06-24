package com.triple.mileage;

import com.triple.mileage.module.domain.*;
import com.triple.mileage.module.place.repository.PlaceRepository;
import com.triple.mileage.module.point.repository.PointRepository;
import com.triple.mileage.module.review.repository.PhotoRepository;
import com.triple.mileage.module.review.repository.ReviewRepository;
import com.triple.mileage.module.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Component
@Transactional
public class TestUtils {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    PointRepository pointRepository;
    @Autowired
    PhotoRepository photoRepository;
    @Autowired
    PlaceRepository placeRepository;


    public User createUser() {
        UUID uuid = UUID.randomUUID();
        User user = new User(uuid);
        return userRepository.save(user);
    }

    public Review createReview(User reviewer, Place place) {
        Review review = new Review(UUID.randomUUID(), TestConst.REVIEW_CONTENT, reviewer, place);
        return reviewRepository.saveAndFlush(review);
    }

    public Photo createPhoto() { // without review
        UUID uuid = UUID.randomUUID();
        Photo photo = Photo.createPhotoWithoutReview(uuid, TestConst.PHOTO_PATH);
        return photoRepository.save(photo);
    }

    public Place createPlace() {
        UUID uuid = UUID.randomUUID();
        Place place = new Place(uuid, TestConst.ADDRESS);
        return placeRepository.save(place);
    }

    public PointHistory createPointHistory(Review review) {
        PointHistory pointHistory = new PointHistory(review.getId(), review.getReviewer(), TestConst.POINT_TYPE, TestConst.POINT_ACTION, TestConst.POINT_VALUE);
        return pointRepository.save(pointHistory);
    }
}
