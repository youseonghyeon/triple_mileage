package com.triple.mileage.infra;

import com.triple.mileage.module.domain.Photo;
import com.triple.mileage.module.domain.Place;
import com.triple.mileage.module.domain.User;
import com.triple.mileage.module.place.repository.PlaceRepository;
import com.triple.mileage.module.review.dto.EventDto;
import com.triple.mileage.module.review.repository.PhotoRepository;
import com.triple.mileage.module.review.repository.ReviewRepository;
import com.triple.mileage.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@Transactional
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final PlaceRepository placeRepository;


    public void init(EventDto eventDto) {
        UUID userId = eventDto.getUserId();
        if (!userRepository.existsById(userId)) {
            User user = new User(userId);
            userRepository.saveAndFlush(user);
        }
        List<UUID> photoIds = eventDto.getAttachedPhotoIds();
        for (UUID photoId : photoIds) {
            if (!photoRepository.existsById(photoId)) {
                Photo photo = Photo.createPhotoWithoutReview(photoId, "https://www.");
                photoRepository.saveAndFlush(photo);
            }
        }
        UUID placeId = eventDto.getPlaceId();
        if (!placeRepository.existsById(placeId)) {
            Place place = new Place(placeId, "임시 주소");
            placeRepository.saveAndFlush(place);
        }

    }
}
