package com.api_pagamentos.dto;

import java.util.UUID;

public record UserDTO(
        UUID id,
        String email,
        String name,
        String phoneNumber,
        String companyId
) {}


