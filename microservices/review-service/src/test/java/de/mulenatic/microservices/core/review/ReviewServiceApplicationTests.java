package de.mulenatic.microservices.core.review;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import de.mulenatic.api.core.review.Review;
import de.mulenatic.microservices.core.review.persistence.ReviewRepository;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT, properties = {"spring.datasource.url=jdbc:h2:mem:review-db"})

public class ReviewServiceApplicationTests {

    @Autowired
    private WebTestClient client;

    @Autowired
    private ReviewRepository repository;

    @Before
    public void setupDb() {
	repository.deleteAll();
    }


    
    @Test
    public void contextLoads() {
    }

    
    @Test
    public void getReviewsByProductId() {

	int productId = 1;

	client.get()
	    .uri("/review?productId=" + productId)
	    .accept(MediaType.APPLICATION_JSON_UTF8)
	    .exchange()
	    .expectStatus().isOk()
	    .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
	    .expectBody()
	    .jsonPath("$.length()").isEqualTo(3)
	    .jsonPath("$[0].productId").isEqualTo(productId);
    }

    @Test
    public void duplicateError() {

	int productId = 1;
	int reviewId = 1;

	assertEquals(0, repository.count());

	postAndVerifyReview(productId, reviewId, HttpStatus.OK)
	    .jsonPath("$.productId").isEqualTo(productId)
	    .jsonPath("$.reviewId").isEqualTo(reviewId);

	assertEquals(1, repository.count());

	postAndVerifyReview(productId, reviewId, HttpStatus.UNPROCESSABLE_ENTITY)
	    .jsonPath("$.path").isEqualTo("/review")
	    .jsonPath("$.message").isEqualTo("Duplicate key, Product Id: 1, Review Id:1");

	assertEquals(1, repository.count());
    }

    @Test
    public void deleteReviews() {

	int productId = 1;
	int recommendationId = 1;

	postAndVerifyReview(productId, recommendationId, HttpStatus.OK);
	assertEquals(1, repository.findByProductId(productId).size());

	deleteAndVerifyReviewsByProductId(productId, HttpStatus.OK);
	assertEquals(0, repository.findByProductId(productId).size());

	deleteAndVerifyReviewsByProductId(productId, HttpStatus.OK);
    }


    @Test
    public void getReviewsMissingParameter() {

	client.get()
	    .uri("/review")
	    .accept(MediaType.APPLICATION_JSON_UTF8)
	    .exchange()
	    .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
	    .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
	    .expectBody()
	    .jsonPath("$.path").isEqualTo("/review")
	    .jsonPath("$.message").isEqualTo("Required int parameter 'productId' is not present");
    }

    @Test
    public void getReviewsInvalidParameter() {

	client.get()
	    .uri("/review?productId=no-integer")
	    .accept(MediaType.APPLICATION_JSON_UTF8)
	    .exchange()
	    .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
	    .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
	    .expectBody()
	    .jsonPath("$.path").isEqualTo("/review")
	    .jsonPath("$.message").isEqualTo("Type mismatch.");
    }

    @Test
    public void getReviewsNotFound() {

	int productIdNotFound = 213;

	client.get()
	    .uri("/review?productId=" + productIdNotFound)
	    .accept(MediaType.APPLICATION_JSON_UTF8)
	    .exchange()
	    .expectStatus().isOk()
	    .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
	    .expectBody()
	    .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    public void getReviewsInvalidParameterNegativeValue() {

	int productIdInvalid = -1;

	client.get()
	    .uri("/review?productId=" + productIdInvalid)
	    .accept(MediaType.APPLICATION_JSON_UTF8)
	    .exchange()
	    .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
	    .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
	    .expectBody()
	    .jsonPath("$.path").isEqualTo("/review")
	    .jsonPath("$.message").isEqualTo("Invalid productId: " + productIdInvalid);
    }

    private WebTestClient.BodyContentSpec getAndVerifyReviewsByProductId(int productId, HttpStatus expectedStatus) {
	return getAndVerifyReviewsByProductId("?productId=" + productId, expectedStatus);
    }

    private WebTestClient.BodyContentSpec getAndVerifyReviewsByProductId(String productIdQuery, HttpStatus expectedStatus) {
	return client.get()
	    .uri("/review" + productIdQuery)
	    .accept(MediaType.APPLICATION_JSON_UTF8)
	    .exchange()
	    .expectStatus().isEqualTo(expectedStatus)
	    .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
	    .expectBody();
    }

    private WebTestClient.BodyContentSpec postAndVerifyReview(int productId, int reviewId, HttpStatus expectedStatus) {

	Review review = new Review(productId, reviewId, "Author " + reviewId, "Subject " + reviewId, "Content " + reviewId, "SA");
	return client.post()
	    .uri("/review")
	    .body(Mono.just(review), Review.class)
	    .accept(MediaType.APPLICATION_JSON_UTF8)
	    .exchange()
	    .expectStatus().isEqualTo(expectedStatus)
	    .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
	    .expectBody();
    }

    

    private WebTestClient.BodyContentSpec deleteAndVerifyReviewsByProductId(int productId, HttpStatus expectedStatus) {
	return client.delete()
	    .uri("/review?productId=" + productId)
	    .accept(MediaType.APPLICATION_JSON_UTF8)
	    .exchange()
	    .expectStatus().isEqualTo(expectedStatus)
	    .expectBody();
    }
}
