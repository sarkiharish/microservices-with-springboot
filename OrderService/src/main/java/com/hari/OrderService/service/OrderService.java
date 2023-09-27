package com.hari.OrderService.service;

import com.hari.OrderService.external.response.OrderResponse;
import com.hari.OrderService.model.OrderRequest;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(long orderId);
}
