package com.api_pagamentos.dto;

import java.util.UUID;

public record PaymentRequestDTO(
        UUID userId,
        double amountCents,
        String paymentMethodId
) {}
