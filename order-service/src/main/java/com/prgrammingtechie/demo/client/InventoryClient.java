package com.prgrammingtechie.demo.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

//CHECK URL IN APPLICATION.PROPERTIES FILE
@FeignClient(name = "inventory-service", url = "${inventory.service.url}")
public interface InventoryClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/inventory")
    boolean isInStock(@RequestParam String skuCode, @RequestParam int quantity);

    @RequestMapping(method = RequestMethod.POST, value = "/api/inventory")
    String updateInventory(@RequestParam String skuCode, @RequestParam int quantity);
}
