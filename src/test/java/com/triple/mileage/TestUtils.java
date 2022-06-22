package com.triple.mileage;

import com.triple.mileage.domain.*;
import com.triple.mileage.place.repository.PlaceRepository;
import com.triple.mileage.point.repository.PointRepository;
import com.triple.mileage.review.dto.EventDto;
import com.triple.mileage.review.repository.PhotoRepository;
import com.triple.mileage.review.repository.ReviewRepository;
import com.triple.mileage.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.util.List;
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

    public EventDto createEventDto(User user, Place place, UUID reviewId, List<UUID> photoIds) {
        EventDto eventDto = new EventDto();
        eventDto.setType(EventType.REVIEW);
        eventDto.setAction(EventAction.ADD);
        eventDto.setReviewId(reviewId);
        eventDto.setContent(TestConst.REVIEW_CONTENT);
        eventDto.setAttachedPhotoIds(photoIds);
        eventDto.setUserId(user.getId());
        eventDto.setPlaceId(place.getId());
        return eventDto;
    }


    public User createUser() {
        User user = new User();
        userRepository.save(user);
        return user;
    }

    public Review createReview(User reviewer, Place place) {
        Review review = new Review(UUID.randomUUID(), TestConst.REVIEW_CONTENT, reviewer, place);
        reviewRepository.save(review);
        return review;
    }

    public Photo createPhoto(Review review) {
        Photo photo = Photo.createPhoto(TestConst.PHOTO_PATH, review);
        photoRepository.save(photo);
        return photo;
    }

    public Photo createPhoto() { // without review
        Photo photo = Photo.createPhotoWithoutReview(TestConst.PHOTO_PATH);
        photoRepository.save(photo);
        return photo;
    }

    public Place createPlace() {
        Place place = new Place(TestConst.ADDRESS);
        placeRepository.save(place);
        return place;
    }

    public PointHistory createPointHistory(Review review) {
        PointHistory pointHistory = new PointHistory(review.getId(), review.getReviewer(), TestConst.POINT_TYPE, TestConst.POINT_ACTION, TestConst.POINT_VALUE);
        pointRepository.save(pointHistory);
        return pointHistory;
    }
}
