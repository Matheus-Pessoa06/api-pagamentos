package com.api_pagamentos.dto;

public record PaymentMethodDTO(
        String name,
        boolean enabled
) {}
