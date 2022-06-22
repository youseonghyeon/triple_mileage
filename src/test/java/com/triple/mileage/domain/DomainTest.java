package com.triple.mileage.domain;

import com.triple.mileage.TestConst;
import com.triple.mileage.TestUtils;
import com.triple.mileage.place.repository.PlaceRepository;
import com.triple.mileage.point.repository.PointRepository;
import com.triple.mileage.review.repository.PhotoRepository;
import com.triple.mileage.review.repository.ReviewRepository;
import com.triple.mileage.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DDL 작성 및 테스트를 위한 도메인테스트
 */
@SpringBootTest
@Transactional
class DomainTest {


    @Autowired
    TestUtils testUtils;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PhotoRepository photoRepository;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    PointRepository pointRepository;
    @Autowired
    PlaceRepository placeRepository;


    @AfterEach
    void reset() {
        photoRepository.deleteAll();
        pointRepository.deleteAll();
        reviewRepository.deleteAll();
        placeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("User 생성")
    void createUser() {
        //when
        User user = testUtils.createUser();
        User findUser = userRepository.findById(user.getId()).orElseThrow();
        //then
        assertNotNull(findUser.getId());
        assertEquals(findUser.getId().getClass(), UUID.class);
        assertEquals(findUser.getMileage(), 0);
        assertNotNull(findUser.getCreatedDate());
        assertNotNull(findUser.getModifiedDate());
    }

    @Test
    @DisplayName("Review 생성")
    void createReview() {
        //given
        User user = testUtils.createUser();
        Place place = testUtils.createPlace();
        //when
        Review review = testUtils.createReview(user, place);
        Review findReview = reviewRepository.findById(review.getId()).orElseThrow();
        //then
        assertNotNull(findReview.getId());
        assertEquals(findReview.getId().getClass(), UUID.class);
        assertEquals(findReview.getContent(), TestConst.REVIEW_CONTENT);
        assertNotNull(findReview.getCreatedDate());
        assertNotNull(findReview.getModifiedDate());
        assertEquals(findReview.getPlace(), place);
        assertEquals(findReview.getReviewer(), user);
    }

    @Test
    @DisplayName("Place 생성")
    void createPlace() {
        //when
        Place place = testUtils.createPlace();
        //then
        assertNotNull(place.getId());
        assertEquals(place.getId().getClass(), UUID.class);
        assertEquals(place.getAddress(), TestConst.ADDRESS);
        assertNotNull(place.getCreatedDate());
        assertNotNull(place.getModifiedDate());
    }

    @Test
    @DisplayName("Photo 생성")
    void createPhotoTest() {
        // given
        User user = testUtils.createUser();
        Place place = testUtils.createPlace();
        Review review = testUtils.createReview(user, place);
        //when
        Photo photo = testUtils.createPhoto(review);
        //then
        assertNotNull(photo.getId());
        assertEquals(photo.getId().getClass(), UUID.class);
        assertEquals(photo.getPath(), TestConst.PHOTO_PATH);
        assertEquals(photo.getReview(), review);
        assertNotNull(photo.getCreatedDate());
        assertNotNull(photo.getModifiedDate());
        assertNull(photo.getCreatedBy());
        assertNull(photo.getModifiedBy());
    }

    @Test
    @DisplayName("Photo 생성(리뷰 없이 단독)")
    void createPhotoWithoutReviewTest() {
        //when
        Photo photo = testUtils.createPhoto();
        //then
        assertNotNull(photo.getId());
        assertEquals(photo.getId().getClass(), UUID.class);
        assertEquals(photo.getPath(), TestConst.PHOTO_PATH);
        assertNull(photo.getReview());
        assertNotNull(photo.getCreatedDate());
        assertNotNull(photo.getModifiedDate());
        assertNull(photo.getCreatedBy());
        assertNull(photo.getModifiedBy());
    }

    @Test
    @DisplayName("PointHistory 생성")
    void createPointHistory() {
        User user = testUtils.createUser();
        Place place = testUtils.createPlace();
        Review review = testUtils.createReview(user, place);
        //when
        PointHistory pointHistory = testUtils.createPointHistory(review);
        //then
        assertNotNull(pointHistory.getId());
        assertEquals(pointHistory.getId().getClass(), UUID.class);
        assertEquals(pointHistory.getAction(), EventAction.valueOf(TestConst.POINT_ACTION));
        assertEquals(pointHistory.getType(), EventType.valueOf(TestConst.POINT_TYPE));
        assertEquals(pointHistory.getValue(), TestConst.POINT_VALUE);
        assertNotNull(pointHistory.getCreatedDate());
        assertNotNull(pointHistory.getModifiedDate());
    }

}
