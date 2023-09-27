package com.hari.PaymentService.service;

import com.hari.PaymentService.entity.TransactionDetails;
import com.hari.PaymentService.model.PaymentMode;
import com.hari.PaymentService.model.PaymentRequest;
import com.hari.PaymentService.model.PaymentResponse;
import com.hari.PaymentService.repository.TransactionDetailsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class PaymentServiceImpl implements PaymentService{

    @Autowired
    private TransactionDetailsRepository transactionDetailsRepository;

    @Override
    public long doPayment(PaymentRequest paymentRequest) {
        log.info("Recording payment details : {}", paymentRequest);

        TransactionDetails transactionDetails = TransactionDetails.builder()
                .paymentDate(Instant.now())
                .paymentMode(paymentRequest.getPaymentMode().name())
                .paymentStatus("SUCCESS")
                .orderId(paymentRequest.getOrderId())
                .referenceNumber(paymentRequest.getReferenceNumber())
                .amount(paymentRequest.getAmount())
                .build();

        transactionDetailsRepository.save(transactionDetails);

        log.info("Transaction completed with id : {}", transactionDetails.getId());

        return transactionDetails.getId();
    }

    @Override
    public PaymentResponse getPaymentDetailsByOrderId(long orderId) {
        log.info("Getting payment details for the order id : {}", orderId);

        TransactionDetails transactionDetails = transactionDetailsRepository.findByOrderId(orderId);

        PaymentResponse paymentResponse = PaymentResponse
                .builder()
                .paymentId(transactionDetails.getId())
                .orderId(transactionDetails.getOrderId())
                .status(transactionDetails.getPaymentStatus())
                .paymentMode(PaymentMode.valueOf(transactionDetails.getPaymentMode()))
                .paymentDate(transactionDetails.getPaymentDate())
                .amount(transactionDetails.getAmount())
                .build();

        return paymentResponse;
    }
}
