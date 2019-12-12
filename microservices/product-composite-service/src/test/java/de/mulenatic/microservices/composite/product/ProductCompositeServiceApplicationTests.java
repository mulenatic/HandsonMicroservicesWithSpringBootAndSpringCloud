package de.mulenatic.microservices.composite.product;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import de.mulenatic.api.core.product.Product;
import de.mulenatic.api.core.recommendation.Recommendation;
import de.mulenatic.api.core.review.Review;
import de.mulenatic.microservices.composite.product.services.ProductCompositeIntegration;
import de.mulenatic.util.exceptions.InvalidInputException;
import de.mulenatic.util.exceptions.NotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ProductCompositeServiceApplicationTests {

    private static final int PRODUCT_ID_OK = 1;
    private static final int PRODUCT_ID_NOT_FOUND = 2;
    private static final int PRODUCT_ID_INVALID = 3;

    @Autowired
    private WebTestClient client;

    @MockBean
    private ProductCompositeIntegration productCompositeIntegration;

    @Before
    public void setUp() {

	Mockito.when(productCompositeIntegration.getProduct(PRODUCT_ID_OK))
	    .thenReturn(new Product(PRODUCT_ID_OK, "mock-address", 1, "name"));
	Mockito.when(productCompositeIntegration.getRecommendations(PRODUCT_ID_OK))
	    .thenReturn(Collections
			.singletonList(new Recommendation(PRODUCT_ID_OK, 1, "author", 1, "content", "mock adress")));
	Mockito.when(productCompositeIntegration.getReviews(PRODUCT_ID_OK))
	    .thenReturn(Collections
			.singletonList(new Review(PRODUCT_ID_OK, 1, "author", "subject", "content", "mock address")));

	Mockito.when(productCompositeIntegration.getProduct(PRODUCT_ID_NOT_FOUND))
	    .thenThrow(new NotFoundException("NOT FOUND: " + PRODUCT_ID_NOT_FOUND));

	Mockito.when(productCompositeIntegration.getProduct(PRODUCT_ID_INVALID))
	    .thenThrow(new InvalidInputException("INVALID: " + PRODUCT_ID_INVALID));	    

    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void getProductById() {
	client.get()
	    .uri("/product-composite/" + PRODUCT_ID_OK)
	    .accept(MediaType.APPLICATION_JSON_UTF8)
	    .exchange()
	    .expectStatus().isOk()
	    .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
	    .expectBody()
	    .jsonPath("$.productId").isEqualTo(PRODUCT_ID_OK).jsonPath("$.recommendations.length()").isEqualTo(1)
	    .jsonPath("$.reviews.length()").isEqualTo(1);
    }

    @Test
    public void getProductNotFound() {
	client.get()
	    .uri("/product-composite/" + PRODUCT_ID_NOT_FOUND)
	    .accept(MediaType.APPLICATION_JSON_UTF8)
	    .exchange()
	    .expectStatus().isNotFound()
	    .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
	    .expectBody()
	    .jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_NOT_FOUND)
	    .jsonPath("$.message").isEqualTo("NOT FOUND: " + PRODUCT_ID_NOT_FOUND);

    }


    @Test
    public void getProductInvalidInput() {

	client.get()
	    .uri("/product-composite/" + PRODUCT_ID_INVALID)
	    .accept(MediaType.APPLICATION_JSON_UTF8)
	    .exchange()
	    .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
	    .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
	    .expectBody()
	    .jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_INVALID)
	    .jsonPath("$.message").isEqualTo("INVALID: " + PRODUCT_ID_INVALID);
    }

    
}
