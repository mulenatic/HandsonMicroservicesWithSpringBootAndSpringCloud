package de.mulenatic.microservices.composite.product;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

@SpringBootApplication
@ComponentScan("de.mulenatic")
@EnableSwagger2WebFlux
public class ProductCompositeServiceApplication {

    public static void main(String[] args) {
	SpringApplication.run(ProductCompositeServiceApplication.class, args);
    }

    @Value("${api.common.title}") String apiTitle;
    @Value("${api.common.description}") String apiDescription;
    @Value("${api.common.version}") String apiVersion;
    @Value("${api.common.termsOfServiceUrl}") String apiTermsOfServiceUrl;
    @Value("${api.common.contact.name}") String apiContactName;
    @Value("${api.common.contact.url}") String apiContactURL;
    @Value("${api.common.contact.email}") String apiContactEmail;
    @Value("${api.common.license}") String apiLicense;
    @Value("${api.common.licenseUrl}") String apiLicenseUrl;

    @Bean
    RestTemplate restTemplate() {
	return new RestTemplate();
    }

    @Bean
    public Docket apiDocumentation() {
	return new Docket(DocumentationType.SWAGGER_2)
	    .select()
	    .apis(RequestHandlerSelectors.basePackage("de.mulenatic.composite.product"))
	    .paths(PathSelectors.any())
	    .build()
	    .globalResponseMessage(RequestMethod.GET, Collections.emptyList())
	    .apiInfo(new ApiInfo(apiTitle, apiDescription, apiVersion, apiTermsOfServiceUrl,
				 new Contact(apiContactName, apiContactURL, apiContactEmail), apiLicense, apiLicenseUrl,
				 Collections.emptyList()));
    }

}
