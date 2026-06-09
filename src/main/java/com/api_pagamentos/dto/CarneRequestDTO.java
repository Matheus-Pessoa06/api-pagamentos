package com.api_pagamentos.dto;

import java.util.UUID;

public record CarneRequestDTO(
        UUID userId,
        UUID paymentValueId
) {}
