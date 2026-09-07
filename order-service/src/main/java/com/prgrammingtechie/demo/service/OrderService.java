package com.prgrammingtechie.demo.service;

import com.prgrammingtechie.demo.client.InventoryClient;
import com.prgrammingtechie.demo.dto.OrderRequest;
import com.prgrammingtechie.demo.model.Order;
import com.prgrammingtechie.demo.repo.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    private final InventoryClient inventoryClient;

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
        log.info("Order placed successfully with Order ID: {}", order.getId());
        return "Order placed successfully with Order ID: " + order.getId();
    }
}
