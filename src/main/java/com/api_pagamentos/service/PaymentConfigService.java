package com.api_pagamentos.service;

import com.api_pagamentos.domain.Company;
import com.api_pagamentos.domain.PaymentMethod;
import com.api_pagamentos.domain.PaymentValue;
import com.api_pagamentos.domain.Users;
import com.api_pagamentos.dto.PaymentMethodDTO;
import com.api_pagamentos.dto.PaymentValueDTO;
import com.api_pagamentos.dto.UserDTO;
import com.api_pagamentos.repository.CompanyRepository;
import com.api_pagamentos.repository.PaymentMethodRepository;
import com.api_pagamentos.repository.PaymentValueRepository;
import com.api_pagamentos.repository.UserRepository;
import com.stripe.exception.StripeException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentConfigService {

    private final PaymentValueRepository paymentValueRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final StripeService stripeService;

    public PaymentConfigService(
            PaymentValueRepository paymentValueRepository,
            PaymentMethodRepository paymentMethodRepository,
            CompanyRepository companyRepository,
            UserRepository userRepository,
            StripeService stripeService) {
        this.paymentValueRepository = paymentValueRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.stripeService = stripeService;
    }

    @Transactional
    public PaymentMethod createPaymentMethod(PaymentMethodDTO dto) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setName(dto.name());
        paymentMethod.setEnabled(dto.enabled());
        return paymentMethodRepository.save(paymentMethod);
    }

    @Transactional
    public Company createCompany(String companyName, String document) {
        Company company = new Company();
        company.setCompanyName(companyName);
        company.setDocument(document);
        return companyRepository.save(company);
    }

    @Transactional
    public Users createUser(UserDTO dto) {
        Company company = companyRepository.findById(UUID.fromString(dto.companyId()))
                .orElseThrow(() -> new RuntimeException("Company not found"));
        Users user = new Users();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPhoneNumber(dto.phoneNumber());
        user.setCompany(company);
        return userRepository.save(user);
    }

    @Transactional
    public PaymentValue configureNewValue(PaymentValueDTO dto) throws StripeException {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(dto.paymentMethodId())
                .orElseThrow(() -> new RuntimeException("Payment Method not found"));

        String stripePriceId = stripeService.createProductAndPrice("Mensalidade " + paymentMethod.getName(), dto.value());

        PaymentValue paymentValue = new PaymentValue();
        paymentValue.setValue(dto.value());
        paymentValue.setPaymentMethod(paymentMethod);
        paymentValue.setStripePriceId(stripePriceId);

        return paymentValueRepository.save(paymentValue);
    }
}
