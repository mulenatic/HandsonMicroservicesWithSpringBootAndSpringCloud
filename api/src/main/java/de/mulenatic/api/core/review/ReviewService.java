package de.mulenatic.api.core.review;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ReviewService {

    @PostMapping(value = "/review", consumes = "application/json", produces = "application/json")
    Review createReview(@RequestBody Review review);
    
    /**
     * Sample usage: curl $HOST:$PORT/review?productId=1
     *
     * @param productId
     * @return
     */
    @GetMapping(
        value    = "/review",
        produces = "application/json")
    List<Review> getReviews(@RequestParam(value = "productId", required = true) int productId);

    @DeleteMapping(value = "/reviews")
    void deleteReviews(@RequestParam(value = "productId", required = true) int productId);
}
