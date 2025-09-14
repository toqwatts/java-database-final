package com.project.code.Repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {

    // Retrieve reviews for a specific product and store
    List<Review> findByStoreIdAndProductId(Long storeId, Long productId);

    // Retrieve reviews by customer
    List<Review> findByCustomerId(Long customerId);

    // Retrieve reviews for a specific product
    List<Review> findByProductId(Long productId);

    // Retrieve reviews for a specific store
    List<Review> findByStoreId(Long storeId);
}
