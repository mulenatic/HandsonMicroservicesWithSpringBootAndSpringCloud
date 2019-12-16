package de.mulenatic.microservices.core.recommendation;

import static org.junit.Assert.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static reactor.core.publisher.Mono.just;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import de.mulenatic.api.core.recommendation.Recommendation;
import de.mulenatic.microservices.core.recommendation.persistence.RecommendationRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"spring.data.mongodb.port: 0"})
public class RecommendationServiceApplicationTests {

    @Autowired
    private WebTestClient client;

    @Autowired
    private RecommendationRepository repository;

    @Before
    public void setupDb() {
	repository.deleteAll();
    }


    @Test
    public void contextLoads() {
    }

    @Test
    public void getRecommendationsByProductId() {

	int productId = 1;

	client.get()
	    .uri("/recommendation?productId=" + productId)
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
	int recommendationId = 1;

	postAndVerifyRecommendation(productId, recommendationId, OK)
	    .jsonPath("$.productId").isEqualTo(productId)
	    .jsonPath("$.recommendationId").isEqualTo(recommendationId);

	assertEquals(1, repository.count());

	postAndVerifyRecommendation(productId, recommendationId, UNPROCESSABLE_ENTITY)
	    .jsonPath("$.path").isEqualTo("/recommendation")
	    .jsonPath("$.message").isEqualTo("Duplicate key, Product Id: 1, Recommendation Id:1");

	assertEquals(1, repository.count());
    }

    @Test
    public void deleteRecommendations() {

	int productId = 1;
	int recommendationId = 1;

	postAndVerifyRecommendation(productId, recommendationId, OK);
	assertEquals(1, repository.findByProductId(productId).size());

	deleteAndVerifyRecommendationsByProductId(productId, OK);
	assertEquals(0, repository.findByProductId(productId).size());

	deleteAndVerifyRecommendationsByProductId(productId, OK);
    }


    @Test
    public void getRecommendationsMissingParameter() {

	client.get()
	    .uri("/recommendation")
	    .accept(MediaType.APPLICATION_JSON_UTF8)
	    .exchange()
	    .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
	    .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
	    .expectBody()
	    .jsonPath("$.path").isEqualTo("/recommendation")
	    .jsonPath("$.message").isEqualTo("Required int parameter 'productId' is not present");
    }

    @Test
    public void getRecommendationsInvalidParameter() {

	client.get()
	    .uri("/recommendation?productId=no-integer")
	    .accept(MediaType.APPLICATION_JSON_UTF8)
	    .exchange()
	    .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
	    .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
	    .expectBody()
	    .jsonPath("$.path").isEqualTo("/recommendation")
	    .jsonPath("$.message").isEqualTo("Type mismatch.");
    }
    @Test
    public void getRecommendationsNotFound() {

	int productIdNotFound = 113;

	client.get()
	    .uri("/recommendation?productId=" + productIdNotFound)
	    .accept(MediaType.APPLICATION_JSON_UTF8)
	    .exchange()
	    .expectStatus().isOk()
	    .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
	    .expectBody()
	    .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    public void getRecommendationsInvalidParameterNegativeValue() {

	int productIdInvalid = -1;

	client.get()
	    .uri("/recommendation?productId=" + productIdInvalid)
	    .accept(MediaType.APPLICATION_JSON_UTF8)
	    .exchange()
	    .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
	    .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
	    .expectBody()
	    .jsonPath("$.path").isEqualTo("/recommendation")
	    .jsonPath("$.message").isEqualTo("Invalid productId: " + productIdInvalid);
    }

    private WebTestClient.BodyContentSpec getAndVerifyRecommendationsByProductId(int productId, HttpStatus expectedStatus) {
	return getAndVerifyRecommendationsByProductId("?productId=" + productId, expectedStatus);
    }

    private WebTestClient.BodyContentSpec getAndVerifyRecommendationsByProductId(String productIdQuery, HttpStatus expectedStatus) {
	return client.get()
	    .uri("/recommendation" + productIdQuery)
	    .accept(APPLICATION_JSON_UTF8)
	    .exchange()
	    .expectStatus().isEqualTo(expectedStatus)
	    .expectHeader().contentType(APPLICATION_JSON_UTF8)
	    .expectBody();
    }

    private WebTestClient.BodyContentSpec postAndVerifyRecommendation(int productId, int recommendationId, HttpStatus expectedStatus) {
	Recommendation recommendation = new Recommendation(productId, recommendationId, "Author " + recommendationId, recommendationId, "Content " + recommendationId, "SA");
	return client.post()
	    .uri("/recommendation")
	    .body(just(recommendation), Recommendation.class)
	    .accept(APPLICATION_JSON_UTF8)
	    .exchange()
	    .expectStatus().isEqualTo(expectedStatus)
	    .expectHeader().contentType(APPLICATION_JSON_UTF8)
	    .expectBody();
    }

    private WebTestClient.BodyContentSpec deleteAndVerifyRecommendationsByProductId(int productId, HttpStatus expectedStatus) {
	return client.delete()
	    .uri("/recommendation?productId=" + productId)
	    .accept(APPLICATION_JSON_UTF8)
	    .exchange()
	    .expectStatus().isEqualTo(expectedStatus)
	    .expectBody();
    }

}
