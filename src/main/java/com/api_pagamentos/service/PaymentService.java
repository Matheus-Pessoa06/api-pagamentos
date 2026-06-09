package com.api_pagamentos.service;

import com.api_pagamentos.domain.MonthlyPayment;
import com.api_pagamentos.domain.PaymentValue;
import com.api_pagamentos.domain.Users;
import com.api_pagamentos.dto.PaymentRequestDTO;
import com.api_pagamentos.repository.MonthlyPaymentRepository;
import com.api_pagamentos.repository.PaymentValueRepository;
import com.api_pagamentos.repository.UserRepository;
import com.stripe.exception.StripeException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    private final MonthlyPaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PaymentValueRepository paymentValueRepository;
    private final StripeService stripeService;

    public PaymentService(
            MonthlyPaymentRepository paymentRepository,
            UserRepository userRepository,
            PaymentValueRepository paymentValueRepository,
            StripeService stripeService) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.paymentValueRepository = paymentValueRepository;
        this.stripeService = stripeService;
    }

    @Transactional
    public MonthlyPayment processStripePayment(PaymentRequestDTO paymentRequest){
        Users user = userRepository.findById(paymentRequest.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String stripeChargeId = "ch_" + UUID.randomUUID().toString().substring(0, 8);

        MonthlyPayment payment = new MonthlyPayment();
        payment.setUser(user);
        payment.setPaid(true);
        payment.setPaidAt(LocalDateTime.now());
        payment.setExpiresAt(LocalDateTime.now());
        payment.setExternalPaymentId(stripeChargeId);
        payment.setValue(100.00);

        return paymentRepository.save(payment);
    }

    @Transactional
    public List<MonthlyPayment> generate12Payments(UUID userId, UUID paymentValueId) throws StripeException {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PaymentValue paymentValue = paymentValueRepository.findById(paymentValueId)
                .orElseThrow(() -> new RuntimeException("Payment Value not found"));

        if (user.getStripeCustomerId() == null || user.getStripeCustomerId().trim().isEmpty()) {
            String stripeCustId = stripeService.createCustomer(user.getName(), user.getEmail(), user.getPhoneNumber());
            user.setStripeCustomerId(stripeCustId);
            userRepository.save(user);
        }

        List<MonthlyPayment> generatedPayments = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            LocalDateTime dueDate = LocalDateTime.now().plusMonths(i);

            Map<String, String> invoiceResult = stripeService.createInvoice(
                    user.getStripeCustomerId(),
                    paymentValue.getStripePriceId(),
                    dueDate
            );

            MonthlyPayment payment = new MonthlyPayment();
            payment.setUser(user);
            payment.setValue(paymentValue.getValue());
            payment.setPaid(false);
            payment.setExpiresAt(dueDate);
            payment.setExternalPaymentId(invoiceResult.get("invoiceId"));
            payment.setPaymentUrl(invoiceResult.get("paymentUrl"));
            payment.setPaymentMethodType(paymentValue.getPaymentMethod().getName());

            generatedPayments.add(paymentRepository.save(payment));
        }

        return generatedPayments;
    }

    @Transactional(readOnly = true)
    public List<MonthlyPayment> getPaymentsByUser(UUID userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return paymentRepository.findByUser(user);
    }

    @Transactional
    public void markInvoiceAsPaid(String stripeInvoiceId) {
        MonthlyPayment payment = paymentRepository.findByExternalPaymentId(stripeInvoiceId)
                .orElseThrow(() -> new RuntimeException("Payment invoice not found for ID: " + stripeInvoiceId));
        payment.setPaid(true);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);
        System.out.println("-> Fatura " + stripeInvoiceId + " marcada como paga com sucesso no banco local!");
    }
}
