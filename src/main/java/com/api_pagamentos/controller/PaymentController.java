package com.api_pagamentos.controller;

import com.api_pagamentos.domain.MonthlyPayment;
import com.api_pagamentos.dto.CarneRequestDTO;
import com.api_pagamentos.dto.PaymentRequestDTO;
import com.api_pagamentos.service.PaymentService;
import com.stripe.exception.StripeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService){
        this.paymentService = paymentService;
    }

    @PostMapping("/stripe")
    public ResponseEntity<MonthlyPayment> processPayment(@RequestBody PaymentRequestDTO paymentRequestDTO){
        MonthlyPayment payment = paymentService.processStripePayment(paymentRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @PostMapping("/generate-carne")
    public ResponseEntity<List<MonthlyPayment>> generateCarne(@RequestBody CarneRequestDTO request) throws StripeException {
        List<MonthlyPayment> payments = paymentService.generate12Payments(request.userId(), request.paymentValueId());
        return ResponseEntity.status(HttpStatus.CREATED).body(payments);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MonthlyPayment>> getUserPayments(@PathVariable UUID userId) {
        List<MonthlyPayment> payments = paymentService.getPaymentsByUser(userId);
        return ResponseEntity.ok(payments);
    }
}
