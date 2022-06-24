package com.triple.mileage.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.triple.mileage.TestConst;
import com.triple.mileage.TestUtils;
import com.triple.mileage.module.domain.*;
import com.triple.mileage.module.place.repository.PlaceRepository;
import com.triple.mileage.module.point.repository.PointRepository;
import com.triple.mileage.module.review.dto.EventDto;
import com.triple.mileage.module.review.repository.PhotoRepository;
import com.triple.mileage.module.review.repository.ReviewRepository;
import com.triple.mileage.module.review.service.ReviewService;
import com.triple.mileage.module.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class ReviewControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TestUtils testUtils;
    @Autowired
    ReviewService reviewService;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    PointRepository pointRepository;
    @Autowired
    PhotoRepository photoRepository;
    @Autowired
    PlaceRepository placeRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EntityManager em;



    @Test
    @DisplayName("리뷰 추가")
    void addReview() throws Exception {
        //given
        User user = testUtils.createUser();
        Place place = testUtils.createPlace();
        UUID reviewId = UUID.randomUUID();
        List<UUID> photoIds = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Photo photo = testUtils.createPhoto();
            photoIds.add(photo.getId());
        }
        EventDto eventDto = createEventDto(EventAction.ADD, user, place, reviewId, photoIds);

        //when
        mockMvc.perform(post("/events")
                        .content(objectMapper.writeValueAsString(eventDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        //then
        Review findReview = reviewRepository.findById(eventDto.getReviewId()).orElseThrow();
        assertEquals(TestConst.REVIEW_CONTENT, findReview.getContent());
        assertEquals(3, findReview.getPhotos().size());
        assertEquals(user.getId(), findReview.getReviewer().getId());

        User findUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(3, findUser.getMileage()); // 3점 (사진 포함, 첫 리뷰)

    }

    @Test
    @DisplayName("리뷰 수정(사진 제거)")
    void modReview() throws Exception {

        //given
        User user = testUtils.createUser();
        Place place = testUtils.createPlace();
        UUID reviewId = UUID.randomUUID();
        List<UUID> photoIds = new ArrayList<>();
        List<Photo> photos = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Photo photo = testUtils.createPhoto();
            photoIds.add(photo.getId());
            photos.add(photo);
        }
        EventDto eventDto = createEventDto(EventAction.ADD, user, place, reviewId, photoIds);
        reviewService.addReview(user, place, photos, eventDto);
        // 성공

        User findUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(3, findUser.getMileage()); // 3점 (사진 포함, 첫 리뷰)
        em.flush();
        em.clear();

        //given
        String newContent = "새로운 내용";
        List<UUID> emptyIds = new ArrayList<>();
        EventDto newEventDto = createEventDto(EventAction.MOD, findUser, place, reviewId, emptyIds);
        newEventDto.setContent(newContent);
        //when
        mockMvc.perform(post("/events")
                        .content(objectMapper.writeValueAsString(newEventDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        em.flush();
        em.clear();

        //then
        Review review = reviewRepository.findById(newEventDto.getReviewId()).orElseThrow();
        assertEquals(newContent, review.getContent());
        //then
        User findUser2 = userRepository.findById(newEventDto.getUserId()).orElseThrow();
        assertEquals(2, findUser2.getMileage()); // 첫리뷰 작성 후 사진 제거 3 - 1 점

    }

    @Test
    @DisplayName("리뷰 삭제")
    void deleteReview() throws Exception {
        //given
        User user = testUtils.createUser();
        Place place = testUtils.createPlace();
        UUID reviewId = UUID.randomUUID();
        List<UUID> photoIds = new ArrayList<>();
        List<Photo> photos = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Photo photo = testUtils.createPhoto();
            photoIds.add(photo.getId());
            photos.add(photo);
        }
        EventDto eventDto = createEventDto(EventAction.ADD, user, place, reviewId, photoIds);
        reviewService.addReview(user, place, photos, eventDto);


        //when
        EventDto deleteEventDto = createEventDto(EventAction.DELETE, user, place, reviewId, new ArrayList<>());
        mockMvc.perform(post("/events")
                        .content(objectMapper.writeValueAsString(deleteEventDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
;
        //then
        User findUser = userRepository.findById(deleteEventDto.getUserId()).orElseThrow();
        assertEquals(0, findUser.getMileage()); // 마일리지 초기화

    }

    public EventDto createEventDto(EventAction action, User user, Place place, UUID reviewId, List<UUID> photoIds) {
        EventDto eventDto = new EventDto();
        eventDto.setType(EventType.REVIEW);
        eventDto.setAction(action);
        eventDto.setReviewId(reviewId);
        eventDto.setContent(TestConst.REVIEW_CONTENT);
        eventDto.setAttachedPhotoIds(photoIds);
        eventDto.setUserId(user.getId());
        eventDto.setPlaceId(place.getId());
        return eventDto;
    }


}
