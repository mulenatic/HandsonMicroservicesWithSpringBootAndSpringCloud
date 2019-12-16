package de.mulenatic.microservices.core.review.services;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import de.mulenatic.api.core.review.Review;
import de.mulenatic.microservices.core.review.persistence.ReviewEntity;

/**
 * ReviewMapper
 */
@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mappings({ @Mapping(target = "serviceAdress", ignore = true) })
    Review entityToApi(ReviewEntity entity);

    @Mappings({ @Mapping(target = "id", ignore = true), @Mapping(target = "version", ignore = true) })
    ReviewEntity apiToEntity(Review review);

    List<Review> entityListToApiList(List<ReviewEntity> entity);
    List<ReviewEntity> apiListToEntityList(List<Review> api);
}
