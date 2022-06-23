package com.triple.mileage;

import com.triple.mileage.module.domain.*;
import com.triple.mileage.module.place.repository.PlaceRepository;
import com.triple.mileage.module.point.repository.PointRepository;
import com.triple.mileage.module.review.dto.EventDto;
import com.triple.mileage.module.review.repository.PhotoRepository;
import com.triple.mileage.module.review.repository.ReviewRepository;
import com.triple.mileage.module.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
        UUID uuid = UUID.randomUUID();
        User user = new User(uuid);
        userRepository.saveAndFlush(user);
        return user;
    }

    public Review createReview(User reviewer, Place place) {
        Review review = new Review(UUID.randomUUID(), TestConst.REVIEW_CONTENT, reviewer, place);
        reviewRepository.saveAndFlush(review);
        return review;
    }

    public Photo createPhoto(Review review) {
        UUID uuid = UUID.randomUUID();
        Photo photo = Photo.createPhoto(uuid, TestConst.PHOTO_PATH, review);
        photoRepository.saveAndFlush(photo);
        return photo;
    }

    public Photo createPhoto() { // without review
        UUID uuid = UUID.randomUUID();
        Photo photo = Photo.createPhotoWithoutReview(uuid, TestConst.PHOTO_PATH);
        photoRepository.saveAndFlush(photo);
        return photo;
    }

    public Place createPlace() {
        UUID uuid = UUID.randomUUID();
        Place place = new Place(uuid, TestConst.ADDRESS);
        placeRepository.saveAndFlush(place);
        return place;
    }

    public PointHistory createPointHistory(Review review) {
        PointHistory pointHistory = new PointHistory(review.getId(), review.getReviewer(), TestConst.POINT_TYPE, TestConst.POINT_ACTION, TestConst.POINT_VALUE);
        pointRepository.saveAndFlush(pointHistory);
        return pointHistory;
    }
}
