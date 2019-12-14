package de.mulenatic.microservices.core.product.persistence;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * ProductRepository
 */
public interface ProductRepository extends PagingAndSortingRepository<ProductEntity, String>{

    Optional<ProductEntity> findByProductId(int productId);
}
