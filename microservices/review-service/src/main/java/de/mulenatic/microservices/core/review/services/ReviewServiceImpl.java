package de.mulenatic.microservices.core.review.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;

import de.mulenatic.api.core.review.Review;
import de.mulenatic.api.core.review.ReviewService;
import de.mulenatic.microservices.core.review.persistence.ReviewEntity;
import de.mulenatic.microservices.core.review.persistence.ReviewRepository;
import de.mulenatic.util.exceptions.InvalidInputException;
import de.mulenatic.util.http.ServiceUtil;

/**
 * ReviewServiceImpl
 */
@RestController
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private final ReviewRepository repository;
    private final ReviewMapper mapper;

    @Autowired
    public ReviewServiceImpl(ServiceUtil serviceUtil, ReviewRepository repository, ReviewMapper mapper) {
	this.serviceUtil = serviceUtil;
	this.repository = repository;
	this.mapper = mapper;
    }

    @Override
    public Review createReview(Review review) {

	try {

	    ReviewEntity entity = mapper.apiToEntity(review);
	    ReviewEntity newReviewEntity = repository.save(entity);

	    LOG.debug("createReview: created a review entity: {}/{}", review.getProductId(), review.getReviewId());
	    return mapper.entityToApi(newReviewEntity);
	    
	} catch (DataIntegrityViolationException die) {
		    
	    throw new InvalidInputException("Duplicate key, Product id: " + review.getProductId() + " and Review id: " + review.getReviewId());
	    
	}
	

    }

    @Override
    public void deleteReviews(int productId) {

	LOG.debug("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
	repository.deleteAll(repository.findByProductId(productId));

    }

    @Override
    public List<Review> getReviews(int productId) {

	if (productId < 1)
	    throw new InvalidInputException("Invalid product id: " + productId);

	List<ReviewEntity> reviewEntities = repository.findByProductId(productId);
	List<Review> reviews = mapper.entityListToApiList(reviewEntities);
	reviews.forEach(r -> r.setServiceAddress(serviceUtil.getServiceAddress()));

	LOG.debug("getReviews: response size: {}", reviews.size());

	return reviews;

    }



}
