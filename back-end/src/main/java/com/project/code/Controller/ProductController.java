package com.project.code.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductRepository productRepository;
    private final ServiceClass serviceClass;
    private final InventoryRepository inventoryRepository;

    @Autowired
    public ProductController(ProductRepository productRepository,
                             ServiceClass serviceClass,
                             InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.serviceClass = serviceClass;
        this.inventoryRepository = inventoryRepository;
    }

    // 3. Add Product
    @PostMapping
    public Map<String, String> addProduct(@RequestBody Product product) {
        Map<String, String> response = new HashMap<>();
        try {
            if (!serviceClass.validateProduct(product)) {
                response.put("message", "Product already exists.");
                return response;
            }
            productRepository.save(product);
            response.put("message", "Product added successfully.");
        } catch (DataIntegrityViolationException e) {
            response.put("message", "Error: Invalid product data.");
        } catch (Exception e) {
            response.put("message", "Unexpected error: " + e.getMessage());
        }
        return response;
    }

    // 4. Get Product by Id
    @GetMapping("/{id}")
    public Map<String, Object> getProductById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Optional<Product> product = productRepository.findById(id);
        response.put("products", product.orElse(null));
        return response;
    }

    // 5. Update Product
    @PutMapping
    public Map<String, String> updateProduct(@RequestBody Product product) {
        Map<String, String> response = new HashMap<>();
        productRepository.save(product);
        response.put("message", "Product updated successfully.");
        return response;
    }

    // 6. Filter by Category and Name
    @GetMapping("/category/{name}/{category}")
    public Map<String, Object> filterByCategoryProduct(@PathVariable String name,
                                                       @PathVariable String category) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products;

        if (!"null".equalsIgnoreCase(name) && !"null".equalsIgnoreCase(category)) {
            products = productRepository.findProductBySubNameAndCategory(name, category);
        } else if (!"null".equalsIgnoreCase(category)) {
            products = productRepository.findByCategory(category);
        } else if (!"null".equalsIgnoreCase(name)) {
            products = productRepository.findProductBySubName(name);
        } else {
            products = productRepository.findAll();
        }

        response.put("products", products);
        return response;
    }

    // 7. List All Products
    @GetMapping
    public Map<String, Object> listProduct() {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findAll();
        response.put("products", products);
        return response;
    }

    // 8. Get Product by Category and StoreId
    @GetMapping("/filter/{category}/{storeId}")
    public Map<String, Object> getProductByCategoryAndStoreId(@PathVariable String category,
                                                              @PathVariable Long storeId) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findProductByCategory(category, storeId);
        response.put("product", products);
        return response;
    }

    // 9. Delete Product
    @DeleteMapping("/{id}")
    public Map<String, String> deleteProduct(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        if (!serviceClass.validateProductId(id)) {
            response.put("message", "Product not found.");
            return response;
        }

        inventoryRepository.deleteByProductId(id);
        productRepository.deleteById(id);
        response.put("message", "Product deleted successfully.");
        return response;
    }

    // 10. Search Product by Name
    @GetMapping("/searchProduct/{name}")
    public Map<String, Object> searchProduct(@PathVariable String name) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findProductBySubName(name);
        response.put("products", products);
        return response;
    }
}
