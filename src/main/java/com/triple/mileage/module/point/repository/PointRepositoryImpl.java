package com.triple.mileage.module.point.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.triple.mileage.module.domain.PointHistory;
import com.triple.mileage.module.domain.QPointHistory;
import com.triple.mileage.module.domain.QUser;
import com.triple.mileage.module.domain.User;
import com.triple.mileage.module.point.dto.HistoryRes;
import com.triple.mileage.module.point.dto.PointHistoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.querydsl.core.types.Projections.list;
import static com.triple.mileage.module.domain.QPlace.place;
import static com.triple.mileage.module.domain.QPointHistory.pointHistory;
import static com.triple.mileage.module.domain.QReview.review;
import static com.triple.mileage.module.domain.QUser.user;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PointRepositoryImpl implements CustomPointRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PointHistory> findByReviewIdAndUserId(UUID reviewId, UUID receiverId) {
        return queryFactory.selectFrom(pointHistory)
                .where(pointHistory.reviewId.eq(reviewId),
                        pointHistory.receiver.id.eq(receiverId))
                .orderBy(pointHistory.createdDate.asc())
                .fetch();
    }

    @Override
    public int findSumByPlaceIdAndUserId(UUID placeId, UUID userId) {
        return queryFactory.select(pointHistory.value.sum())
                .from(pointHistory)
                .innerJoin(review)
                .on(pointHistory.reviewId.eq(review.id))
                .innerJoin(review.place, place)
                .where(pointHistory.receiver.id.eq(userId),
                        place.id.eq(placeId))
                .fetchOne();
    }

    @Override
    public List<PointHistoryDto> findHistoryDto(UUID userId) {

        return queryFactory.select(Projections.constructor(PointHistoryDto.class,
                        pointHistory.type,
                        pointHistory.action,
                        pointHistory.value,
                        pointHistory.reviewId,
                        pointHistory.createdDate))
                .from(pointHistory)
                .where(pointHistory.receiver.id.eq(userId))
                .orderBy(pointHistory.createdDate.asc())
                .fetch();


    }


}
