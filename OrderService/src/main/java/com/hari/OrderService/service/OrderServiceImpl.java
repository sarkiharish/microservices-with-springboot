package com.hari.OrderService.service;

import com.hari.OrderService.entity.Order;
import com.hari.OrderService.exception.CustomException;
import com.hari.OrderService.external.client.PaymentService;
import com.hari.OrderService.external.client.ProductService;
import com.hari.OrderService.external.request.PaymentRequest;
import com.hari.OrderService.external.response.OrderResponse;
import com.hari.OrderService.external.response.PaymentResponse;
import com.hari.OrderService.external.response.ProductResponse;
import com.hari.OrderService.model.OrderRequest;
import com.hari.OrderService.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public long placeOrder(OrderRequest orderRequest) {
        //Order Entity -> Save the data with Status Order Created
        //Product Service -> Block Products(Reduce the Quantity)
        //Payment Service -> Payments -> Success -> COMPLETE, Else CANCELLED

        log.info("Placing Order Request : {}", orderRequest);

        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());

        log.info("Creating order with status CREATED");
        Order order = Order.builder()
                .amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();

        order = orderRepository.save(order);

        log.info("Calling payment service to complete payment");
        PaymentRequest paymentRequest = PaymentRequest
                        .builder()
                        .orderId(order.getId())
                        .paymentMode(orderRequest.getPaymentMode())
                        .amount(orderRequest.getTotalAmount())
                        .build();

        String orderStatus = null;
        try {
            paymentService.doPayment(paymentRequest);
            log.info("Payment done Successfully. Changing the order status to PLACED");
            orderStatus = "PLACED";
        }catch (Exception e) {
            log.error("Error occurred in payment. Changing order status to PAYMENT_FAILED");
            orderStatus = "PAYMENT_FAILED";
        }

        order.setOrderStatus(orderStatus);
        orderRepository.save(order);

        log.info("Order placed successfully with id : {}", order.getId());
        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Get order details for order id : {}", orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new CustomException("Order with given id not found", "NOT_FOUND", 404)
        );

        log.info("Invoking Product Service to fetch the product for id : {}", order.getProductId());

        ProductResponse productResponse = restTemplate.getForObject(
                "http://PRODUCT-SERVICE/product/" + order.getProductId(),
                ProductResponse.class
        );

        log.info("Getting Payment Information from payment service");
//        PaymentResponse paymentResponse =
//                restTemplate.getForObject(
//                        "http://PAYMENT-SERVICE/payment/order/" + order.getId(),
//                        PaymentResponse.class
//                );

        PaymentResponse paymentResponse = null;

        try {
            paymentResponse = restTemplate.getForObject(
                    "http://PAYMENT-SERVICE/payment/order/" + order.getId(),
                    PaymentResponse.class
            );
        } catch (Exception ex) {
            log.info("Payment not found with Order id: {}", orderId);
            throw new CustomException("Payment not found with Order id: " + orderId, "PAYMENT_NOT_FOUND", 404);
        }

        OrderResponse.ProductDetails productDetails =
                OrderResponse.ProductDetails
                        .builder()
                        .productId(productResponse.getProductId())
                        .productName(productResponse.getProductName())
                        .quantity(order.getQuantity())
                        .price(productResponse.getPrice())
                        .build();

        OrderResponse.PaymentDetails paymentDetails =
                OrderResponse.PaymentDetails
                        .builder()
                        .paymentId(paymentResponse.getPaymentId())
                        .paymentDate(paymentResponse.getPaymentDate())
                        .orderId(paymentResponse.getOrderId())
                        .status(paymentResponse.getStatus())
                        .paymentMode(paymentResponse.getPaymentMode())
                        .amount(paymentResponse.getAmount())
                        .build();

        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(order.getId())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .amount(order.getAmount())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();

        return orderResponse;
    }
}
