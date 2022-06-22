package com.triple.mileage.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.triple.mileage.TestConst;
import com.triple.mileage.TestUtils;
import com.triple.mileage.domain.*;
import com.triple.mileage.review.dto.EventDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Commit;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@Commit
class ReviewControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TestUtils testUtils;

    @Test
    @DisplayName("POST(/events) 컨트롤러 테스트")
    void eventControllerTest() throws Exception {
        //given
        User user = testUtils.createUser();
        Place place = testUtils.createPlace();
        UUID reviewId = UUID.randomUUID();
//        Review review = testUtils.createReview(user, place);
        List<UUID> photoIds = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Photo photo = testUtils.createPhoto();
            photoIds.add(photo.getId());
        }

        EventDto eventDto = createEventDto(user, place, reviewId, photoIds);

        //then
        mockMvc.perform(post("/events")
                .content(objectMapper.writeValueAsString(eventDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    private EventDto createEventDto(User user, Place place, UUID reviewId, List<UUID> photoIds) {
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


}
