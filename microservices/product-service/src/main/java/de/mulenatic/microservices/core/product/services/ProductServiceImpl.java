package de.mulenatic.microservices.core.product.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import de.mulenatic.api.core.product.Product;
import de.mulenatic.api.core.product.ProductService;
import de.mulenatic.microservices.core.product.persistence.ProductEntity;
import de.mulenatic.microservices.core.product.persistence.ProductRepository;
import de.mulenatic.util.exceptions.InvalidInputException;
import de.mulenatic.util.exceptions.NotFoundException;
import de.mulenatic.util.http.ServiceUtil;

/**
 * ProductServiceImpl
 */
@RestController
public class ProductServiceImpl implements ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Autowired
    public ProductServiceImpl(ServiceUtil serviceUtil, ProductRepository repository, ProductMapper mapper) {
	this.serviceUtil = serviceUtil;
	this.repository = repository;
	this.mapper = mapper;
    }

    @Override
    public Product createProduct(Product body) {

	try {
	    ProductEntity entity = mapper.apiToEntity(body);
	    ProductEntity newEntity = repository.save(entity);

	    return mapper.entityToApi(newEntity);
	} catch (DuplicateKeyException dke) {
	    throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId());
	}
	  
    }

    
    

    @Override
    public Product getProduct(@PathVariable int productId) {

	if (productId < 1 ) throw new InvalidInputException("Invalid productId: " + productId);

	ProductEntity entity = repository.findByProductId(productId)
	    .orElseThrow(() -> new NotFoundException("No product found for productId: " + productId));
	Product response = mapper.entityToApi(entity);
	response.setServiceAddress(serviceUtil.getServiceAddress());

	return response;


    }

    @Override
    public void deleteProduct(int productId) {
	repository.findByProductId(productId).ifPresent(e -> repository.delete(e));		
    }

    






    
}
