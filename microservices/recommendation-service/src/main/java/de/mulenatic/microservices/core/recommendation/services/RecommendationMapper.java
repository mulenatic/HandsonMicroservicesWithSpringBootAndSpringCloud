package de.mulenatic.microservices.core.recommendation.services;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import de.mulenatic.api.core.recommendation.Recommendation;
import de.mulenatic.microservices.core.recommendation.persistence.RecommendationEntity;

/**
 * ReommendationMapper
 */
@Mapper(componentModel = "spring")
public interface RecommendationMapper {

    @Mappings({ @Mapping(target = "serviceAdress", ignore = true)})
    Recommendation entityToApi(RecommendationEntity recommendationEntity);

    @Mappings({@Mapping( target = "id", ignore = true), @Mapping(target = "version", ignore = true)})
    RecommendationEntity apiToEntity(Recommendation recommendation);

    List<Recommendation> entityListToApiList(List<RecommendationEntity> recommendationEntitieList);

    List<RecommendationEntity> apiListToEntityList(List<Recommendation> recommendationList);

    
}
