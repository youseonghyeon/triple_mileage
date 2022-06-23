package com.triple.mileage.module.review.repository;

import com.triple.mileage.module.domain.Place;
import com.triple.mileage.module.domain.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional(readOnly = true)
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    @EntityGraph(attributePaths = {"photos"}, type = EntityGraph.EntityGraphType.FETCH)
    Review findWithPhotosById(UUID reviewId);

    @EntityGraph(attributePaths = {"reviewer", "photo"}, type = EntityGraph.EntityGraphType.FETCH)
    Review findWithUserById(UUID reviewId);

    boolean existsByPlace(Place place);

    boolean existsByPlaceId(UUID placeId);

    boolean existsByPlaceIdAndReviewerId(UUID place, UUID reviewerId);

}