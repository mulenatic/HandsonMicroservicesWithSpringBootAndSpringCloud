package de.mulenatic.microservices.core.product;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ProductServiceApplicationTests {

    @Autowired
    private WebTestClient client;

    @Test
    public void contextLoads() {
    }


    @Test
    public void getProductById() {

	int productId = 1;

	client.get()
	    .uri("/product/" + productId)
	    .accept(MediaType.APPLICATION_JSON_UTF8)
	    .exchange()
	    .expectStatus().isOk()
	    .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
	    .expectBody()
	    .jsonPath("$.productId").isEqualTo(productId);
    }

    @Test
    public void getProductInvalidParameterString() {

	client.get()
	    .uri("/product/no-integer")
	    .accept(MediaType.APPLICATION_JSON_UTF8)
	    .exchange()
	    .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
	    .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
	    .expectBody()
	    .jsonPath("$.path").isEqualTo("/product/no-integer")
	    .jsonPath("$.message").isEqualTo("Type mismatch.");
    }

    @Test
    public void getProductNotFound() {

	int productIdNotFound = 13;

	client.get()
	    .uri("/product/" + productIdNotFound)
	    .accept(MediaType.APPLICATION_JSON_UTF8)
	    .exchange()
	    .expectStatus().isNotFound()
	    .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
	    .expectBody()
	    .jsonPath("$.path").isEqualTo("/product/" + productIdNotFound)
	    .jsonPath("$.message").isEqualTo("No product found for productId: " + productIdNotFound);
    }

    @Test
    public void getProductInvalidParameterNegativeValue() {

	int productIdInvalid = -1;

	client.get()
	    .uri("/product/" + productIdInvalid)
	    .accept(MediaType.APPLICATION_JSON_UTF8)
	    .exchange()
	    .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
	    .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
	    .expectBody()
	    .jsonPath("$.path").isEqualTo("/product/" + productIdInvalid)
	    .jsonPath("$.message").isEqualTo("Invalid productId: " + productIdInvalid);
    }
    

}
