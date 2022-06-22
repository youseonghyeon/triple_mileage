package com.triple.mileage.review.controller;

import com.triple.mileage.review.dto.EventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReviewController {

    @PostMapping("/events")
    public void eventHandler(@RequestBody EventDto eventDto) {
        log.info(eventDto.toString());

    }
}
