package com.grandia.orderService.controller;

import com.grandia.orderService.domain.Order;
import com.grandia.orderService.domain.Item;
import com.grandia.orderService.dto.order.CustomerOrderDetails;
import com.grandia.orderService.dto.request.CustomerOrderRequest;
import com.grandia.orderService.remote.ProductServiceClient;
import com.grandia.orderService.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class OrderController {

    private OrderRepository orderRepository;
    private ProductServiceClient productServiceClient;

    @Autowired
    public OrderController(OrderRepository orderRepository, ProductServiceClient productServiceClient) {
        this.orderRepository = orderRepository;
        this.productServiceClient = productServiceClient;
    }

    @GetMapping("/orders")
    public List<CustomerOrderDetails> getCustomerOrders(@RequestParam String customerId) {
        final List<Order> order = orderRepository.findByCustomerId(customerId);
        return order.stream().map(o -> toCustomerOrderDetails(o)).collect(Collectors.toList());
    }

    @GetMapping("/orders/{id}")
    public CustomerOrderDetails getOrders(@PathVariable("id") Long orderId) {
        final Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return null;
        }
        return toCustomerOrderDetails(order);
    }

    private CustomerOrderDetails toCustomerOrderDetails(Order order) {
        return CustomerOrderDetails.builder()
                .orderId(order.getId())
                .createdDate(order.getCreatedDate())
                .externalReference(order.getExternalReference())
                .items(toItemList(order.getItems()))
                .build();
    }

    private List<com.grandia.orderService.dto.product.Item> toItemList(List<Item> items) {
        return items.stream().map(item -> toItemDto(item)).collect(Collectors.toList());
    }

    private com.grandia.orderService.dto.product.Item toItemDto(Item item) {
        return com.grandia.orderService.dto.product.Item
                .builder()
                .product(productServiceClient.getProduct(item.getProductId())).build();
    }

    @PostMapping("/orders")
    public Order save(@RequestBody CustomerOrderRequest request) {
        return orderRepository.save(Order
                .builder()
                .customerId(request.getCustomerId())
                .externalReference(request.getExternalReference())
                .items((request.getItems() == null) ? null : toItems(request.getItems())).build());
    }

    private List<Item> toItems(List<com.grandia.orderService.dto.request.Item> items) {
        return items.stream().map(item -> Item.builder().productId(item.getProductId())
                .quantity(item.getQuantity()).build()).collect(Collectors.toList());
    }
}
