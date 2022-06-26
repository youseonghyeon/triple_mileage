package com.triple.mileage.module.point.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.triple.mileage.module.point.dto.PointHistoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.triple.mileage.module.domain.QPointHistory.pointHistory;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PointRepositoryImpl implements CustomPointRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Integer findSumByReviewId(UUID reviewId) {
        return queryFactory.select(pointHistory.value.sum())
                .from(pointHistory)
                .where(pointHistory.reviewId.eq(reviewId))
                .fetchOne();
    }

    @Override
    public List<PointHistoryDto> findHistoryDto(UUID userId) {

        return queryFactory.select(Projections.constructor(PointHistoryDto.class,
                        pointHistory.type, pointHistory.action, pointHistory.value, pointHistory.createdDate))
                .from(pointHistory)
                .where(pointHistory.receiver.id.eq(userId))
                .orderBy(pointHistory.createdDate.asc())
                .fetch();


    }


}
