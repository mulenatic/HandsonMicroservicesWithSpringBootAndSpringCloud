package de.mulenatic.microservices.composite.product.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import de.mulenatic.api.composite.product.ProductAggregate;
import de.mulenatic.api.composite.product.ProductCompositeService;
import de.mulenatic.api.composite.product.RecommendationSummary;
import de.mulenatic.api.composite.product.ReviewSummary;
import de.mulenatic.api.composite.product.ServiceAddresses;
import de.mulenatic.api.core.product.Product;
import de.mulenatic.api.core.recommendation.Recommendation;
import de.mulenatic.api.core.review.Review;
import de.mulenatic.util.exceptions.NotFoundException;
import de.mulenatic.util.http.ServiceUtil;


/**
 * ProductCompositeService
 */
@RestController
public class ProductCompositeServiceImpl implements ProductCompositeService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private ProductCompositeIntegration productCompositeIntegration;

    @Autowired
    public ProductCompositeServiceImpl(ServiceUtil serviceUtil,
				       ProductCompositeIntegration productCompositeIntegration) {
	this.serviceUtil = serviceUtil;
	this.productCompositeIntegration = productCompositeIntegration;
    }

    @Override
    public ProductAggregate getProduct(int productId) {

	Product product = productCompositeIntegration.getProduct(productId);
	if (product == null)
	    throw new NotFoundException("No product found for productId: " + productId);

	List<Recommendation> recommendations = productCompositeIntegration.getRecommendations(productId);

	List<Review> reviews = productCompositeIntegration.getReviews(productId);

	return createProductAggregate(product, recommendations, reviews, serviceUtil.getServiceAddress());
    }

    private ProductAggregate createProductAggregate(Product product, List<Recommendation> recommendations,
						    List<Review> reviews, String serviceAddress) {

	// 1. Setup product info
	int productId = product.getProductId();
	String name = product.getName();
	int weight = product.getWeight();

	// 2. Copy summary recommendation info, if available
	List<RecommendationSummary> recommendationSummaries = (recommendations == null) ? null
	    : recommendations.stream()
	    .map(r -> new RecommendationSummary(r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent()))
	    .collect(Collectors.toList());

	// 3. Copy summary review info, if available
	List<ReviewSummary> reviewSummaries = (reviews == null) ? null
	    : reviews.stream().map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent()))
	    .collect(Collectors.toList());

	// 4. Create info regarding the involved microservices addresses
	String productAddress = product.getServiceAddress();
	String reviewAddress = (reviews != null && reviews.size() > 0) ? reviews.get(0).getServiceAddress() : "";
	String recommendationAddress = (recommendations != null && recommendations.size() > 0)
	    ? recommendations.get(0).getServiceAddress()
	    : "";
	ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, productAddress, reviewAddress,
								 recommendationAddress);

	return new ProductAggregate(productId, name, weight, recommendationSummaries, reviewSummaries,
				    serviceAddresses);
    }

    @Override
    public void createCompositeProduct(ProductAggregate body) {

	try {

	    Product product = new Product(body.getProductId(), body.getName(), body.getWeight(), null );
	    productCompositeIntegration.createProduct(product);

	    if (body.getRecommendations() != null ) {

		body.getRecommendations().forEach(r -> {
			Recommendation recommendation = new Recommendation(body.getProductId(), r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent(), null);
			productCompositeIntegration.createRecommendation(recommendation);
		    });
		
	    }

	    if (body.getReviews() != null) {

		body.getReviews().forEach(r -> {
			Review review = new Review(body.getProductId(), r.getReviewId(), r.getAuthor(), r.getSubject(),	r.getContent(), null);
			productCompositeIntegration.createReview(review);
		    });
	    }

	} catch (RuntimeException re) {

	    LOG.warn("createCompositeProduct failed", re);
	    throw re;

	}
		
    }

    @Override
    public void deleteCompositeProduct(int productId) {
	// TODO Auto-generated method stub
		
    }

    

}
