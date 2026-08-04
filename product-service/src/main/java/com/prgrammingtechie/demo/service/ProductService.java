package com.prgrammingtechie.demo.service;

import com.prgrammingtechie.demo.dto.ProductResponse;
import com.prgrammingtechie.demo.model.Product;
import com.prgrammingtechie.demo.repo.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public ProductResponse createProduct(com.prgrammingtechie.demo.dto.ProductRequest productRequest) {
        com.prgrammingtechie.demo.model.Product product = com.prgrammingtechie.demo.model.Product.builder()
                .id(productRequest.id())
                .name(productRequest.name())
                .description(productRequest.description())
                .price(productRequest.price())
                .build();
        productRepository.save(product);
        log.info("Product created.");
        return convertProductToProductResponse(product);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream().map(this::convertProductToProductResponse).toList();
    }

    //helper method to convert Product to ProductResponse
    public ProductResponse convertProductToProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice()
        );
    }
}
