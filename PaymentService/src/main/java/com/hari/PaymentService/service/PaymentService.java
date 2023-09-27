package com.hari.PaymentService.service;

import com.hari.PaymentService.model.PaymentRequest;
import com.hari.PaymentService.model.PaymentResponse;

public interface PaymentService {
    long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetailsByOrderId(long orderId);
}
