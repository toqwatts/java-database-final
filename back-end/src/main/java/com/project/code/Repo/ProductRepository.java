package com.project.code.Repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Retrieve all products
    List<Product> findAll();

    // Retrieve products by category
    List<Product> findByCategory(String category);

    // Paginated version: Retrieve products by category
    Page<Product> findByCategory(String category, Pageable pageable);

    // Retrieve products within a price range
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    // Paginated version: Retrieve products within a price range
    Page<Product> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);

    // Retrieve a product by its SKU
    Product findBySku(String sku);

    // Retrieve a product by its name
    Product findByName(String name);

    // Retrieve products by name pattern for a specific store (custom query)
    @Query("SELECT p FROM Product p JOIN Inventory i ON p.id = i.product.id WHERE i.store.id = :storeId AND p.name LIKE %:pname%")
    List<Product> findByNameLikeForStore(@Param("storeId") Long storeId, @Param("pname") String pname);

    // Paginated version of custom query
    @Query("SELECT p FROM Product p JOIN Inventory i ON p.id = i.product.id WHERE i.store.id = :storeId AND p.name LIKE %:pname%")
    Page<Product> findByNameLikeForStore(@Param("storeId") Long storeId, @Param("pname") String pname, Pageable pageable);
}