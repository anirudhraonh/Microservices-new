package com.prgrammingtechie.demo.service;

import com.prgrammingtechie.demo.client.InventoryClient;
import com.prgrammingtechie.demo.dto.OrderRequest;
import com.prgrammingtechie.demo.model.Order;
import com.prgrammingtechie.demo.repo.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final RedisTemplate<String, Object> redisTemplate;

    public String placeOrder(OrderRequest orderRequest) {

        if (!inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity())) {
            return "Inventory not available for SKU: " + orderRequest.skuCode();
        }

        Order order = Order.builder()
                .skuCode(orderRequest.skuCode())
                .price(orderRequest.price())
                .quantity(orderRequest.quantity())
                .build();

        orderRepository.save(order);
        
        // Cache the order in Redis with 1 hour expiration
        String cacheKey = "order:" + order.getId();
        redisTemplate.opsForValue().set(cacheKey, order, 1, TimeUnit.HOURS);
        
        log.info("Order placed successfully with Order ID: {}", order.getId());
        return "Order placed successfully with Order ID: " + order.getId();
    }

    @Cacheable(value = "orders", key = "#id")
    public Order getOrderById(Long id) {
        log.info("Fetching order with ID: {}", id);
        return orderRepository.findById(id).orElse(null);
    }

    @CacheEvict(value = "orders", key = "#id")
    public void deleteOrder(Long id) {
        log.info("Deleting order with ID: {}", id);
        orderRepository.deleteById(id);
    }
}

