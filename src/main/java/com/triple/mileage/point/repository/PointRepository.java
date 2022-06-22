package com.triple.mileage.point.repository;

import com.triple.mileage.domain.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional(readOnly = true)
public interface PointRepository extends JpaRepository<PointHistory, UUID>, CustomPointRepository {

}
