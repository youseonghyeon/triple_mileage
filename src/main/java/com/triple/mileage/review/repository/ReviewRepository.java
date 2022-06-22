package com.triple.mileage.review.repository;

import com.triple.mileage.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional(readOnly = true)
public interface ReviewRepository extends JpaRepository<Review, UUID> {

}
