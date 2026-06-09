package com.api_pagamentos.controller;

import com.api_pagamentos.domain.Company;
import com.api_pagamentos.domain.PaymentMethod;
import com.api_pagamentos.domain.PaymentValue;
import com.api_pagamentos.domain.Users;
import com.api_pagamentos.dto.CompanyDTO;
import com.api_pagamentos.dto.PaymentMethodDTO;
import com.api_pagamentos.dto.PaymentValueDTO;
import com.api_pagamentos.dto.UserDTO;
import com.api_pagamentos.service.PaymentConfigService;
import com.stripe.exception.StripeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments/config")
public class PaymentConfigController {

    private final PaymentConfigService configService;

    public PaymentConfigController(PaymentConfigService configService) {
        this.configService = configService;
    }

    @PostMapping("/methods")
    public ResponseEntity<PaymentMethod> createPaymentMethod(@RequestBody PaymentMethodDTO dto) {
        PaymentMethod paymentMethod = configService.createPaymentMethod(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentMethod);
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@RequestBody CompanyDTO dto) {
        Company company = configService.createCompany(dto.companyName(), dto.document());
        return ResponseEntity.status(HttpStatus.CREATED).body(company);
    }

    @PostMapping("/users")
    public ResponseEntity<Users> createUser(@RequestBody UserDTO dto) {
        Users user = configService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/values")
    public ResponseEntity<PaymentValue> configureValue(@RequestBody PaymentValueDTO dto) throws StripeException {
        PaymentValue paymentValue = configService.configureNewValue(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentValue);
    }
}
