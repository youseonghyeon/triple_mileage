package com.triple.mileage.module.point.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.triple.mileage.module.domain.PointHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.triple.mileage.domain.QPlace.place;
import static com.triple.mileage.domain.QPointHistory.pointHistory;
import static com.triple.mileage.domain.QReview.review;

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
}