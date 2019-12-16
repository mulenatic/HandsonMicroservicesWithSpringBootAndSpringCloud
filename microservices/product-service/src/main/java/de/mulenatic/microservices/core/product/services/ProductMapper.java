package de.mulenatic.microservices.core.product.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import de.mulenatic.api.core.product.Product;
import de.mulenatic.microservices.core.product.persistence.ProductEntity;

/**
 * ProductMapper
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mappings({@Mapping(target = "serviceAddress", ignore = true)})
    Product entityToApi(ProductEntity entity);

    @Mappings({@Mapping(target = "id", ignore = true), @Mapping(target = "version", ignore = true)})
    ProductEntity apiToEntity(Product api);
}
