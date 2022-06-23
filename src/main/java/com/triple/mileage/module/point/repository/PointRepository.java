package com.triple.mileage.module.point.repository;

import com.triple.mileage.module.domain.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional(readOnly = true)
public interface PointRepository extends JpaRepository<PointHistory, UUID>, CustomPointRepository {

    List<PointHistory> findByReceiverIdOrderByCreatedDateAsc(UUID userId);

}
