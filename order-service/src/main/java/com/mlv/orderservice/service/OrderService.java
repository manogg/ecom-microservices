package com.mlv.orderservice.service;

import com.mlv.orderservice.dto.InventoryResponse;
import com.mlv.orderservice.dto.OrderLineItemsDto;
import com.mlv.orderservice.dto.OrderRequest;
import com.mlv.orderservice.entity.Order;
import com.mlv.orderservice.entity.OrderLineItems;
import com.mlv.orderservice.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WebClient webClient;

    public void placeOrder(OrderRequest orderRequest) {

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList().stream().map(this::mapToDto).toList();
        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList()
                .stream().map(orderLineItems1 -> orderLineItems1.getSkuCode()).toList();

        //Call the Inventory Service and check the stock and then place order

        InventoryResponse[] inventoryResponses = webClient.get()
                .uri("http://localhost:8082/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean isInStock = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isIsInStock);

        if (isInStock) {
            orderRepository.save(order);
            log.info("class::orderService >>> Orders placed successfully {}", orderRequest);
        } else {

            throw new IllegalArgumentException("Oh Noh..One of item(s) is an Out Of Stock");
        }

    }
    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {

        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setId(orderLineItemsDto.getId());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}

