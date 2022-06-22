package com.triple.mileage.point.service;

import com.triple.mileage.domain.PointHistory;
import com.triple.mileage.domain.Review;
import com.triple.mileage.domain.User;
import com.triple.mileage.point.repository.PointRepository;
import com.triple.mileage.review.dto.EventDto;
import com.triple.mileage.review.repository.ReviewRepository;
import com.triple.mileage.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public void saveAndGiveMileage(Review review, EventDto eventDto, int mileage) {
        PointHistory history = new PointHistory(review, eventDto.getType(), eventDto.getAction(), mileage);
        pointRepository.save(history);
        User reviewer = review.getReviewer();
        reviewer.plusMileage(mileage);
    }

    public void saveAndGiveMileage(EventDto eventDto, int mileage) {
        Review review = reviewRepository.findById(eventDto.getReviewId()).orElseThrow();
        saveAndGiveMileage(review, eventDto, mileage);
    }
}
