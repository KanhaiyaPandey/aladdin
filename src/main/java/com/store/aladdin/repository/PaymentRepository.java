package com.store.aladdin.repository;

import com.store.aladdin.models.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
}
