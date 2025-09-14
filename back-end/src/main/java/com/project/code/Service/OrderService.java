package com.project.code.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OrderService {

    private final CustomerRepository customerRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(CustomerRepository customerRepository,
                        StoreRepository storeRepository,
                        ProductRepository productRepository,
                        InventoryRepository inventoryRepository,
                        OrderDetailsRepository orderDetailsRepository,
                        OrderItemRepository orderItemRepository) {
        this.customerRepository = customerRepository;
        this.storeRepository = storeRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderDetailsRepository = orderDetailsRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public void saveOrder(PlaceOrderRequestDTO placeOrderRequest) {
        // Step 2: Retrieve or Create Customer
        Customer customer = customerRepository.findByEmail(placeOrderRequest.getCustomerEmail());
        if (customer == null) {
            customer = new Customer(
                    placeOrderRequest.getCustomerName(),
                    placeOrderRequest.getCustomerEmail(),
                    placeOrderRequest.getCustomerPhone()
            );
            customerRepository.save(customer);
        }

        // Step 3: Retrieve Store
        Store store = storeRepository.findById(placeOrderRequest.getStoreId())
                .orElseThrow(() -> new RuntimeException("Store not found with ID: " + placeOrderRequest.getStoreId()));

        // Step 4: Create OrderDetails
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setCustomer(customer);
        orderDetails.setStore(store);
        orderDetails.setDate(LocalDateTime.now());
        orderDetails.setTotalPrice(placeOrderRequest.getTotalPrice());

        orderDetails = orderDetailsRepository.save(orderDetails);

        // Step 5: Create and Save OrderItems
        for (PlaceOrderRequestDTO.OrderProductDTO productDTO : placeOrderRequest.getProducts()) {
            // Retrieve Inventory
            Inventory inventory = inventoryRepository.findByProductIdandStoreId(productDTO.getProductId(), store.getId());
            if (inventory == null || inventory.getStockLevel() < productDTO.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product ID: " + productDTO.getProductId());
            }

            // Update stock
            inventory.setStockLevel(inventory.getStockLevel() - productDTO.getQuantity());
            inventoryRepository.save(inventory);

            // Create OrderItem
            Product product = productRepository.findById(productDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productDTO.getProductId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(orderDetails);
            orderItem.setProduct(product);
            orderItem.setQuantity(productDTO.getQuantity());
            orderItem.setPrice(productDTO.getPrice());

            orderItemRepository.save(orderItem);
        }
    }
}
