package com.triple.mileage.module.place.repository;

import com.triple.mileage.module.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional(readOnly = true)
public interface PlaceRepository extends JpaRepository<Place, UUID> {
}
