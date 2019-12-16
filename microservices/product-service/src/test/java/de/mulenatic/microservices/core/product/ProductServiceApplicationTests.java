package de.mulenatic.microservices.core.product;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

import de.mulenatic.api.core.product.Product;
import de.mulenatic.microservices.core.product.persistence.ProductRepository;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = {"spring.data.mongodb.port: 0"})
public class ProductServiceApplicationTests {

    @Autowired
    private WebTestClient client;

    @Autowired
    private ProductRepository repository;

    @Before
    public void setupDb() {
	repository.deleteAll();
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void duplicateError() {
	int productId = 1;
	postAndVerifyProduct(productId, HttpStatus.OK);
	assertTrue(repository.findByProductId(productId).isPresent());

	postAndVerifyProduct(productId, HttpStatus.UNPROCESSABLE_ENTITY)
	    .jsonPath("$.path").isEqualTo("/product")
	    .jsonPath("$.message").isEqualTo("Duplicate key, Product id: " + productId);

    }

    @Test
    public void deleteProduct() {

	int productId = 1;
	postAndVerifyProduct(productId, HttpStatus.OK);
	assertTrue(repository.findByProductId(productId).isPresent());
	
	deleteAndVerifyProduct(productId, HttpStatus.OK);
	assertFalse(repository.findByProductId(productId).isPresent());

	deleteAndVerifyProduct(productId, HttpStatus.OK);

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

    private WebTestClient.BodyContentSpec getAndVerifyProduct(int productId, HttpStatus expectedStatus) {
	return getAndVerifyProduct("/" + productId, expectedStatus);
    }

    private WebTestClient.BodyContentSpec getAndVerifyProduct(String productIdPath, HttpStatus expectedStatus) {
	return client.get()
	    .uri("/product" + productIdPath)
	    .accept(MediaType.APPLICATION_JSON_UTF8)
	    .exchange()
	    .expectStatus().isEqualTo(expectedStatus)
	    .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
	    .expectBody();
    }

    private WebTestClient.BodyContentSpec postAndVerifyProduct(int productId, HttpStatus expectedStatus) {

	Product product = new Product(productId, "Name " + productId, productId, "SA");
	return client.post().uri("/product").body(Mono.just(product), Product.class)
	    .accept(MediaType.APPLICATION_JSON_UTF8).exchange().expectStatus().isEqualTo(expectedStatus)
	    .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8).expectBody();

    }

    private WebTestClient.BodyContentSpec deleteAndVerifyProduct(int productId, HttpStatus expectedStatus) {
	return client.delete()
	    .uri("/product/" + productId)
	    .accept(MediaType.APPLICATION_JSON_UTF8)
	    .exchange()
	    .expectStatus().isEqualTo(expectedStatus)
	    .expectBody();
    }  
    

}
