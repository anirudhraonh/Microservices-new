package com.prgrammingtechie.demo.dto;

import java.math.BigDecimal;

public record OrderRequest(String orderNumber, String skuCode, BigDecimal price, Integer quantity) {
}
