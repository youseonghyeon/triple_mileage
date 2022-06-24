package com.triple.mileage.module.point.policy;

import com.triple.mileage.module.domain.Review;
import com.triple.mileage.module.point.repository.PointRepository;
import com.triple.mileage.module.review.dto.EventDto;
import com.triple.mileage.module.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

/**
 * 마일리지 정책
 */
@Component
@RequiredArgsConstructor
public class MileagePolicy {

    private final ReviewRepository reviewRepository;
    private final PointRepository pointRepository;


    public int addReviewMileage(EventDto eventDto) {
        int mileage = 0;
        if (hasContent(eventDto.getContent())) {
            mileage += 1;
        }
        if (firstCustomer(eventDto.getPlaceId())) {
            mileage += 1;
        }
        if (hasPhoto(eventDto.getAttachedPhotoIds())) {
            mileage += 1;
        }
        return mileage;
    }
    private boolean hasContent(String content) {
        return StringUtils.hasText(content);
    }

    private boolean firstCustomer(UUID placeId) {
        return !reviewRepository.existsByPlaceId(placeId);
    }

    private boolean hasPhoto(List<UUID> photoIds) {
        return !photoIds.isEmpty();
    }

    public int modifyReviewMileage(Review review, EventDto eventDto) {
        int beforeCnt = review.getPhotos().size(); // 수정 전 사진 개수
        int afterCnt = eventDto.getAttachedPhotoIds().size(); // 수정 후 사진 개수
        String beforeContent = review.getContent();
        String afterContent = eventDto.getContent();
        int mileage = 0;
        if (addFirstPhoto(beforeCnt, afterCnt)) {
            mileage += 1;
        } else if (removeAllPhoto(beforeCnt, afterCnt)) {
            mileage -= 1;
        }
        if (addContent(beforeContent, afterContent)) {
            mileage += 1;
        }
        if (removeContent(beforeContent, afterContent)) {
            mileage -= 1;
        }
        return mileage;
    }

    private boolean removeContent(String beforeContent, String afterContent) {
        return StringUtils.hasText(beforeContent) && !StringUtils.hasText(afterContent);
    }

    private boolean addContent(String beforeContent, String afterContent) {
        return !StringUtils.hasText(beforeContent) && StringUtils.hasText(afterContent);
    }


    private boolean addFirstPhoto(int before, int after) {
        return before == 0 && after > 0;
    }

    private boolean removeAllPhoto(int before, int after) {
        return before > 0 && after == 0;
    }

    public int deleteReviewMileage(UUID placeId, UUID userId) {
        return pointRepository.findSumByPlaceIdAndUserId(placeId, userId);
    }
}
