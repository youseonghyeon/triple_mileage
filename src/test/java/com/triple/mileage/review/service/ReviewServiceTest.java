package com.triple.mileage.review.service;

import com.triple.mileage.TestConst;
import com.triple.mileage.TestUtils;
import com.triple.mileage.module.domain.*;
import com.triple.mileage.module.place.repository.PlaceRepository;
import com.triple.mileage.module.point.dto.PointHistoryDto;
import com.triple.mileage.module.point.repository.PointRepository;
import com.triple.mileage.module.review.dto.EventDto;
import com.triple.mileage.module.review.repository.PhotoRepository;
import com.triple.mileage.module.review.repository.ReviewRepository;
import com.triple.mileage.module.review.service.ReviewService;
import com.triple.mileage.module.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class ReviewServiceTest {

    @Autowired
    ReviewService reviewService;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    PointRepository pointRepository;
    @Autowired
    TestUtils testUtils;
    @Autowired
    PhotoRepository photoRepository;
    @Autowired
    PlaceRepository placeRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EntityManager em;


    @Test
    @DisplayName("첫번째 리뷰 추가(사진 미포함)")
    void addFirstReview() {
        //given
        User user = testUtils.createUser();
        Place place = testUtils.createPlace();

        EventDto eventDto = createEventDto(user, place, null);
        //when
        UUID uuid = reviewService.addReview(user, place, new ArrayList<>(), eventDto);
        //then
        Review review = reviewRepository.findById(uuid).orElseThrow();
        assertEquals(user.getId(), review.getReviewer().getId());
        assertEquals(TestConst.REVIEW_CONTENT, review.getContent());
        assertEquals(place.getId(), review.getPlace().getId());
        assertEquals(0, review.getPhotos().size());
        assertNotNull(review.getCreatedDate());
        assertNotNull(review.getModifiedDate());
        //then
        PointHistory history = pointRepository.findByReviewIdAndUserId(review.getId(), user.getId()).get(0);
        User findUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(2, findUser.getMileage());
        assertEquals(2, history.getValue());
        assertEquals(EventType.valueOf(TestConst.POINT_TYPE), history.getType());
        assertEquals(EventAction.valueOf(TestConst.POINT_ACTION), history.getAction());
        assertEquals(user.getId(), history.getReceiver().getId());
        assertNotNull(history.getCreatedDate());
        assertNotNull(history.getModifiedDate());
    }

    @Test
    @DisplayName("첫번째 리뷰 추가(사진 1개 포함)")
    void addFirstReviewWithPhoto() {
        /**
         * 첫번째 리뷰 2점
         * 사진 추가 1점
         * 총 3점
         */
        //given
        User user = testUtils.createUser();
        Place place = testUtils.createPlace();
        Photo photo = testUtils.createPhoto();
        List<Photo> photos = new ArrayList<>();
        photos.add(photo);

        EventDto eventDto = createEventDto(user, place, photo);
        //when
        UUID uuid = reviewService.addReview(user, place, photos, eventDto);
        em.flush();
        em.clear();

        //then
        Review review = reviewRepository.findById(uuid).orElseThrow();
        assertEquals(user.getId(), review.getReviewer().getId());
        assertEquals(TestConst.REVIEW_CONTENT, review.getContent());
        assertEquals(place.getId(), review.getPlace().getId());
        assertEquals(1, review.getPhotos().size());
        assertNotNull(review.getCreatedDate());
        assertNotNull(review.getModifiedDate());
        //then
        PointHistory history = pointRepository.findByReviewIdAndUserId(review.getId(), user.getId()).get(0);
        User findUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(3, findUser.getMileage());
        assertEquals(3, history.getValue());
        assertEquals(EventType.valueOf(TestConst.POINT_TYPE), history.getType());
        assertEquals(EventAction.valueOf(TestConst.POINT_ACTION), history.getAction());
        assertEquals(user.getId(), history.getReceiver().getId());
        assertNotNull(history.getCreatedDate());
        assertNotNull(history.getModifiedDate());
    }


    @Test
    @DisplayName("두번째 리뷰 추가(사진 포함)")
    void addReviewWithPhoto() {
        /**
         * 두번째 리뷰 1점
         * 사진 추가 1점
         */
        //given
        User user2 = testUtils.createUser();
        Place place = testUtils.createPlace();
        User user = testUtils.createUser();
        Photo photo = testUtils.createPhoto();
        List<Photo> photos = new ArrayList<>();
        photos.add(photo);
        // 첫번째 mock review
        testUtils.createReview(user2, place);

        EventDto eventDto = createEventDto(user, place, photo);
        //when
        UUID uuid = reviewService.addReview(user, place, photos, eventDto);

        //then
        Review review = reviewRepository.findById(uuid).orElseThrow();
        assertEquals(user.getId(), review.getReviewer().getId());
        assertEquals(TestConst.REVIEW_CONTENT, review.getContent());
        assertEquals(place.getId(), review.getPlace().getId());
        assertEquals(1, review.getPhotos().size());
        assertNotNull(review.getCreatedDate());
        assertNotNull(review.getModifiedDate());
        //then
        PointHistory history = pointRepository.findByReviewIdAndUserId(review.getId(), user.getId()).get(0);
        User findUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(2, findUser.getMileage());
        assertEquals(2, history.getValue());
        assertEquals(EventType.valueOf(TestConst.POINT_TYPE), history.getType());
        assertEquals(EventAction.valueOf(TestConst.POINT_ACTION), history.getAction());
        assertEquals(user.getId(), history.getReceiver().getId());
        assertNotNull(history.getCreatedDate());
        assertNotNull(history.getModifiedDate());
    }


    @Test
    @DisplayName("리뷰 수정(사진 추가)")
    void modifyReview_사진추가() {
        /**
         * 첫 리뷰(사진X) 2점
         * 사진 추가 1점
         */
        //given
        User user = testUtils.createUser();
        Place place = testUtils.createPlace();
        EventDto eventDto = createEventDto(user, place, null);
        UUID uuid = reviewService.addReview(user, place, new ArrayList<>(), eventDto);
        Review findReview = reviewRepository.findById(uuid).orElseThrow();


        Photo photo = testUtils.createPhoto();
        List<Photo> newPhotos = new ArrayList<>();
        newPhotos.add(photo);
        String newContent = "수정된 내용";
        EventDto modifyEventDto = createModEventDto(uuid, user, place, photo, newContent);
        //when
        reviewService.modifyReview(findReview, newPhotos, modifyEventDto);
        //then
        Review review = reviewRepository.findById(uuid).orElseThrow();
        assertEquals(newContent, review.getContent());
        assertEquals(1, review.getPhotos().size());
        //then
        List<PointHistory> histories = pointRepository.findByReviewIdAndUserId(review.getId(), user.getId());
        User findUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(2, histories.size());
        PointHistory history = histories.get(1);
        assertEquals(3, findUser.getMileage()); // 2 + 1점
        assertEquals(2, histories.get(0).getValue());
        assertEquals(1, history.getValue()); // 1점 추가
    }

    @Test
    @DisplayName("리뷰 수정(사진 제거)")
    void modifyReview_사진삭제() {
        /**
         * 첫 리뷰 3점
         * 사진 제거 -1점
         */
        //given
        User user = testUtils.createUser();
        Place place = testUtils.createPlace();
        Photo photo = testUtils.createPhoto();
        List<Photo> photos = new ArrayList<>();
        photos.add(photo);
        EventDto eventDto = createEventDto(user, place, photo);
        UUID uuid = reviewService.addReview(user, place, photos, eventDto);
        Review findReview = reviewRepository.findById(uuid).orElseThrow();


        String newContent = "수정된 내용";
        EventDto modifyEventDto = createModEventDto(uuid, user, place, null, newContent);
        //when
        reviewService.modifyReview(findReview, new ArrayList<>(), modifyEventDto);

        em.flush();
        em.clear();

        //then
        Review review = reviewRepository.findById(uuid).orElseThrow();
        assertEquals(newContent, review.getContent());
        assertEquals(0, review.getPhotos().size());

        //then
        List<PointHistory> histories = pointRepository.findByReviewIdAndUserId(review.getId(), user.getId());
        User findUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(2, histories.size());
        assertEquals(2, findUser.getMileage()); // 3 - 1점
        assertEquals(3, histories.get(0).getValue());
        assertEquals(-1, histories.get(1).getValue()); // 1점 제거
    }

    @Test
    @DisplayName("리뷰 삭제(수정포함)")
    void deleteReview() {
        /**
         * 첫 리뷰 3점
         * 사진 제거 -1점
         * 리뷰 삭제 -2점
         */
        //given
        User user = testUtils.createUser();
        Place place = testUtils.createPlace();
        Photo photo = testUtils.createPhoto();
        List<Photo> photos = new ArrayList<>();
        photos.add(photo);
        EventDto eventDto = createEventDto(user, place, photo);
        UUID uuid = reviewService.addReview(user, place, photos, eventDto);
        Review findReview = reviewRepository.findById(uuid).orElseThrow();


        String newContent = "수정된 내용";
        EventDto modifyEventDto = createModEventDto(uuid, user, place, null, newContent);
        reviewService.modifyReview(findReview, new ArrayList<>(), modifyEventDto);
        EventDto deleteEventDto = createDeleteEventDto(uuid, user, place);
        //when
        reviewService.deleteReview(findReview, deleteEventDto);
        em.flush();
        em.clear();

        //then
        List<PointHistoryDto> histories = pointRepository.findHistoryDto(user.getId());
        assertEquals(3, histories.size());
        assertEquals(3, histories.get(0).getValue()); // 3점 추가
        assertEquals(-1, histories.get(1).getValue()); // 1점 제거
        assertEquals(-2, histories.get(2).getValue()); // 2점 제거
        assertEquals(0, user.getMileage()); // 3 -1 -2점
    }

    @Test
    @DisplayName("내용 없는 리뷰(사진 미포함)")
    void noContentReview() {
        /**
         * 첫번째 리뷰   1점
         * 내용(x)       0점
         * 사진 미포함   0점
         */
        //given
        User user = testUtils.createUser();
        Place place = testUtils.createPlace();

        EventDto eventDto = new EventDto();
        eventDto.setReviewId(UUID.randomUUID());
        eventDto.setType(EventType.REVIEW);
        eventDto.setAction(EventAction.ADD);
        eventDto.setUserId(user.getId());
        eventDto.setPlaceId(place.getId());
        //when
        UUID uuid = reviewService.addReview(user, place, new ArrayList<>(), eventDto);
        //then
        Review review = reviewRepository.findById(uuid).orElseThrow();
        assertEquals(user.getId(), review.getReviewer().getId());
        assertNull(review.getContent());
        assertEquals(place.getId(), review.getPlace().getId());
        assertEquals(0, review.getPhotos().size());
        assertNotNull(review.getCreatedDate());
        assertNotNull(review.getModifiedDate());
        //then
        PointHistory history = pointRepository.findByReviewIdAndUserId(review.getId(), user.getId()).get(0);
        User findUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(1, findUser.getMileage());
        assertEquals(1, history.getValue());
        assertEquals(EventType.valueOf(TestConst.POINT_TYPE), history.getType());
        assertEquals(EventAction.valueOf(TestConst.POINT_ACTION), history.getAction());
        assertEquals(user.getId(), history.getReceiver().getId());
        assertNotNull(history.getCreatedDate());
        assertNotNull(history.getModifiedDate());
    }


    private EventDto createDeleteEventDto(UUID uuid, User user, Place place) {
        EventDto eventDto = new EventDto();
        eventDto.setReviewId(uuid);
        eventDto.setType(EventType.REVIEW);
        eventDto.setAction(EventAction.DELETE);
        eventDto.setUserId(user.getId());
        eventDto.setPlaceId(place.getId());
        return eventDto;
    }


    private EventDto createEventDto(User user, Place place, Photo photo) {
        EventDto eventDto = new EventDto();
        eventDto.setReviewId(UUID.randomUUID());
        eventDto.setType(EventType.REVIEW);
        eventDto.setAction(EventAction.ADD);
        eventDto.setUserId(user.getId());
        if (photo != null) {
            eventDto.getAttachedPhotoIds().add(photo.getId());
        }
        eventDto.setContent(TestConst.REVIEW_CONTENT);
        eventDto.setPlaceId(place.getId());
        return eventDto;
    }

    private EventDto createModEventDto(UUID reviewId, User user, Place place, Photo photo, String content) {
        EventDto eventDto = new EventDto();
        eventDto.setReviewId(reviewId);
        eventDto.setType(EventType.REVIEW);
        eventDto.setAction(EventAction.MOD);
        eventDto.setUserId(user.getId());
        if (photo != null) {
            eventDto.getAttachedPhotoIds().add(photo.getId());
        }
        eventDto.setContent(content);
        eventDto.setPlaceId(place.getId());
        return eventDto;
    }
}
