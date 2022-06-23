package com.triple.mileage.module.review.repository;

import com.triple.mileage.module.domain.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional(readOnly = true)
public interface PhotoRepository extends JpaRepository<Photo, UUID> {
}
