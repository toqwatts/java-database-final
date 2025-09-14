package com.project.code.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/store")
public class StoreController {

    private final StoreRepository storeRepository;
    private final OrderService orderService;

    @Autowired
    public StoreController(StoreRepository storeRepository, OrderService orderService) {
        this.storeRepository = storeRepository;
        this.orderService = orderService;
    }

    // 3. Add Store
    @PostMapping
    public Map<String, String> addStore(@RequestBody Store store) {
        storeRepository.save(store);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Store created successfully with ID: " + store.getId());
        return response;
    }

    // 4. Validate Store
    @GetMapping("/validate/{storeId}")
    public boolean validateStore(@PathVariable Long storeId) {
        return storeRepository.existsById(storeId);
    }

    // 5. Place Order
    @PostMapping("/placeOrder")
    public Map<String, String> placeOrder(@RequestBody PlaceOrderRequestDTO placeOrderRequest) {
        Map<String, String> response = new HashMap<>();
        try {
            orderService.saveOrder(placeOrderRequest);
            response.put("message", "Order placed successfully!");
        } catch (Exception e) {
            response.put("Error", "Failed to place order: " + e.getMessage());
        }
        return response;
    }
}