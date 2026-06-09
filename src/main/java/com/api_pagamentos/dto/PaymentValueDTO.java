package com.api_pagamentos.dto;

import java.util.UUID;

public record PaymentValueDTO(
        Double value,
        UUID paymentMethodId
) {}
