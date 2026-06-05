package com.api_pagamentos.service;


import com.api_pagamentos.domain.MonthlyPayment;
import com.api_pagamentos.domain.Users;
import com.api_pagamentos.dto.PaymentRequestDTO;
import com.api_pagamentos.repository.MonthlyPaymentRepository;
import com.api_pagamentos.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final MonthlyPaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public PaymentService(MonthlyPaymentRepository paymentRepository, UserRepository userRepository){
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public MonthlyPayment processStripePayment(PaymentRequestDTO paymentRequest){
        Users user = userRepository.findById(paymentRequest.userId())
                .orElseThrow(() -> new RuntimeException(("User not found")));

        String stripeChargeId = "ch_" + UUID.randomUUID().toString().substring(0, 8);

        MonthlyPayment payment = new MonthlyPayment();
        payment.setUser(user);
        payment.setPayed(true);
        payment.setPayedAt(LocalDateTime.now());
        payment.setExpiresAt(LocalDateTime.now());
        payment.setExternalPaymentId((stripeChargeId));


        return paymentRepository.save(payment);
    }


}
