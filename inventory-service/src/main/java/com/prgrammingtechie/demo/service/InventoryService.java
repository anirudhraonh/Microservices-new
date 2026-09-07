package com.prgrammingtechie.demo.service;

import com.prgrammingtechie.demo.dto.InventoryRequest;
import com.prgrammingtechie.demo.model.InventoryItem;
import com.prgrammingtechie.demo.repo.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public void addInventory(InventoryRequest inventoryRequest) {
        InventoryItem inventoryItem = InventoryItem.builder()
                .skuCode(inventoryRequest.skuCode())
                .quantity(inventoryRequest.quantity())
                .build();

        inventoryRepository.save(inventoryItem);
        log.info("Inventory added for SKU: {}", inventoryItem.getSkuCode());
    }

    public Boolean isInStock(String skuCode, Long quantity) {
        return inventoryRepository.existsBySkuCodeAndQuantityGreaterThanEqual(skuCode, quantity);
    }

    public String updateInventory(String skuCode, Long quantity) {
        var inventoryItem = inventoryRepository.findBySkuCode(skuCode);

        if (inventoryItem.isEmpty()) {
            return "Inventory item not found for SKU: " + skuCode;
        }

        InventoryItem item = inventoryItem.get();
        if (item.getQuantity() < quantity) {
            return "Insufficient inventory for SKU: " + skuCode;
        }

        item.setQuantity(item.getQuantity() - quantity);
        inventoryRepository.save(item);
        log.info("Inventory updated for SKU: {}. New quantity: {}", skuCode, item.getQuantity());
        return "Inventory updated for SKU: " + skuCode + ". New quantity: " + item.getQuantity();
    }

}
