package com.hari.PaymentService.repository;

import com.hari.PaymentService.entity.TransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionDetailsRepository extends JpaRepository<TransactionDetails, Long> {
    TransactionDetails findByOrderId(long orderId);
}
