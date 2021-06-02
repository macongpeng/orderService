package com.grandia.orderService.remote;

import com.grandia.orderService.dto.product.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "localhost:8001")
public interface ProductServiceClient {

    @GetMapping("/products/{id}")
    Product getProduct(@PathVariable("id") Long id);
}
