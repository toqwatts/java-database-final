package com.project.code.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ServiceClass serviceClass;

    @Autowired
    public InventoryController(ProductRepository productRepository,
                               InventoryRepository inventoryRepository,
                               ServiceClass serviceClass) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.serviceClass = serviceClass;
    }

    // 3. Update Inventory
    @PutMapping("/update")
    public Map<String, String> updateInventory(@RequestBody CombinedRequest request) {
        Map<String, String> response = new HashMap<>();

        if (!serviceClass.validateProductId(request.getProduct().getId())) {
            response.put("message", "Invalid product ID.");
            return response;
        }

        Inventory existingInventory = serviceClass.getInventoryId(request.getInventory());
        if (existingInventory != null) {
            existingInventory.setStockLevel(request.getInventory().getStockLevel());
            inventoryRepository.save(existingInventory);
            response.put("message", "Inventory updated successfully.");
        } else {
            response.put("message", "No inventory data available for this product and store.");
        }

        return response;
    }

    // 4. Save Inventory
    @PostMapping("/save")
    public Map<String, String> saveInventory(@RequestBody Inventory inventory) {
        Map<String, String> response = new HashMap<>();

        if (!serviceClass.validateInventory(inventory)) {
            response.put("message", "Inventory already exists for this product in the store.");
            return response;
        }

        inventoryRepository.save(inventory);
        response.put("message", "Inventory saved successfully.");
        return response;
    }

    // 5. Get All Products for a Store
    @GetMapping("/products/{storeId}")
    public Map<String, List<Product>> getAllProducts(@PathVariable Long storeId) {
        Map<String, List<Product>> response = new HashMap<>();
        List<Inventory> inventoryList = inventoryRepository.findByStore_Id(storeId);

        List<Product> products = new ArrayList<>();
        for (Inventory inv : inventoryList) {
            products.add(inv.getProduct());
        }

        response.put("products", products);
        return response;
    }

    // 6. Get Product by Category and Name
    @GetMapping("/filter")
    public Map<String, List<Product>> getProductName(@RequestParam String category,
                                                     @RequestParam String name) {
        Map<String, List<Product>> response = new HashMap<>();
        List<Product> products;

        if (!"null".equalsIgnoreCase(category) && !"null".equalsIgnoreCase(name)) {
            products = productRepository.findByCategory(category);
            products.removeIf(product -> !product.getName().equalsIgnoreCase(name));
        } else if (!"null".equalsIgnoreCase(category)) {
            products = productRepository.findByCategory(category);
        } else if (!"null".equalsIgnoreCase(name)) {
            Product p = productRepository.findByName(name);
            products = p != null ? Collections.singletonList(p) : new ArrayList<>();
        } else {
            products = productRepository.findAll();
        }

        response.put("product", products);
        return response;
    }

    // 7. Search Product in a Store by Name
    @GetMapping("/search")
    public Map<String, List<Product>> searchProduct(@RequestParam Long storeId,
                                                    @RequestParam String name) {
        Map<String, List<Product>> response = new HashMap<>();
        List<Product> products = productRepository.findByNameLike(storeId, name);
        response.put("product", products);
        return response;
    }

    // 8. Remove Product by ID
    @DeleteMapping("/remove/{productId}")
    public Map<String, String> removeProduct(@PathVariable Long productId) {
        Map<String, String> response = new HashMap<>();

        if (!serviceClass.validateProductId(productId)) {
            response.put("message", "Product not found.");
            return response;
        }

        // Delete product and related inventory
        productRepository.deleteById(productId);
        inventoryRepository.deleteByProductId(productId);

        response.put("message", "Product and related inventory deleted successfully.");
        return response;
    }

    // 9. Validate Quantity in Store
    @GetMapping("/validateQuantity")
    public Map<String, String> validateQuantity(@RequestParam Long storeId,
                                                @RequestParam Long productId,
                                                @RequestParam Integer quantity) {
        Map<String, String> response = new HashMap<>();

        Inventory inventory = inventoryRepository.findByProductIdandStoreId(productId, storeId);
        if (inventory == null) {
            response.put("message", "No inventory found for this product in the store.");
        } else if (inventory.getStockLevel() < quantity) {
            response.put("message", "Insufficient stock. Available: " + inventory.getStockLevel());
        } else {
            response.put("message", "Sufficient stock available.");
        }

        return response;
    }
}
