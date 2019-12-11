package de.mulenatic.microservices.composite.product.services;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import de.mulenatic.api.core.product.Product;
import de.mulenatic.api.core.product.ProductService;
import de.mulenatic.api.core.recommendation.Recommendation;
import de.mulenatic.api.core.recommendation.RecommendationService;
import de.mulenatic.api.core.review.Review;
import de.mulenatic.api.core.review.ReviewService;

/**
 * ProductCompositeIntegration
 */
@Component
public class ProductCompositeIntegration implements ProductService, RecommendationService, ReviewService {

	private final RestTemplate restTemplate;
	private final ObjectMapper mapper;

	private final String productServiceUrl;
	private final String recommendationServiceUrl;
	private final String reviewServiceUrl;

	@Autowired
	public ProductCompositeIntegration(RestTemplate restTemplate, ObjectMapper mapper,
			@Value("${app.product-service.host}") String productServiceHost,
			@Value("$(app.product-service.port)") String productServicePort,
			@Value("${app.review-service.host}") String reviewServiceHost,
			@Value("$(app.review-service.port)") String reviewServicePort,
			@Value("${app.recommendation-service.host}") String recommendationServiceHost,
			@Value("$(app.recommendation-service.port)") String recommendationServicePort) {
		this.restTemplate = restTemplate;
		this.mapper = mapper;
		this.productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/product/";
		this.recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation?productId=";
		this.reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review?productId=";
	}

	@Override
	public Product getProduct(int productId) {
		String url = productServiceUrl + productId;
		Product product = restTemplate.getForObject(url, Product.class);
		return product;
	}

	@Override
	public List<Recommendation> getRecommendations(int productId) {
		String url = recommendationServiceUrl + productId;
		List<Recommendation> recommendations = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Recommendation>>() {
		}).getBody();
		return recommendations;
	}

	@Override
	public List<Review> getReviews(int productId) {
		String url = reviewServiceUrl + productId;
		List<Review> reviews = restTemplate
				.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Review>>() {
				}).getBody();
		return reviews;
	}

    

}
