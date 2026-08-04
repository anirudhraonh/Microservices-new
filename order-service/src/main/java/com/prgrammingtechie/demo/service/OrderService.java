package com.prgrammingtechie.demo.service;

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

    public void placeOrder(OrderRequest orderRequest) {
        Order order = Order.builder()
                .orderNumber(orderRequest.orderNumber())
                .skuCode(orderRequest.skuCode())
                .price(orderRequest.price())
                .quantity(orderRequest.quantity())
                .build();

        orderRepository.save(order);
        log.info("Order placed successfully: {}", order.getOrderNumber());
    }
}
