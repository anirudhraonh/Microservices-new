package com.prgrammingtechie.demo.controller;

import com.prgrammingtechie.demo.dto.InventoryRequest;
import com.prgrammingtechie.demo.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String addInventory(@RequestBody InventoryRequest inventoryRequest) {
        inventoryService.addInventory(inventoryRequest);
        return "Inventory added successfully";
    }

    @GetMapping("/{skuCode}")
    public Boolean isInStock(@PathVariable String skuCode, @RequestParam Long quantity) {
        return inventoryService.isInStock(skuCode, quantity);
    }
}
