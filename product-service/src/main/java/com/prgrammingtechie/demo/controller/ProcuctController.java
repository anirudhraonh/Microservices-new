package com.prgrammingtechie.demo.controller;

import com.prgrammingtechie.demo.dto.ProductRequest;
import com.prgrammingtechie.demo.dto.ProductResponse;
import com.prgrammingtechie.demo.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProcuctController {

    private final com.prgrammingtechie.demo.service.ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse craeteProduct(@RequestBody ProductRequest productRequest) {
        return productService.createProduct(productRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }
}
