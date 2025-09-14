package com.project.code.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public ReviewController(ReviewRepository reviewRepository,
                            CustomerRepository customerRepository) {
        this.reviewRepository = reviewRepository;
        this.customerRepository = customerRepository;
    }

    // 3. Get Reviews for a Product in a Store
    @GetMapping("/{storeId}/{productId}")
    public Map<String, Object> getReviews(@PathVariable Long storeId,
                                          @PathVariable Long productId) {
        Map<String, Object> response = new HashMap<>();

        // Fetch reviews for the given store and product
        List<Review> reviews = reviewRepository.findByStoreIdAndProductId(storeId, productId);

        // Transform reviews to include only comment, rating, and customerName
        List<Map<String, Object>> filteredReviews = new ArrayList<>();
        for (Review review : reviews) {
            Map<String, Object> reviewData = new HashMap<>();
            reviewData.put("comment", review.getComment());
            reviewData.put("rating", review.getRating());

            customerRepository.findById(review.getCustomerId()).ifPresent(customer -> {
                reviewData.put("customerName", customer.getName());
            });

            filteredReviews.add(reviewData);
        }

        response.put("reviews", filteredReviews);
        return response;
    }
}