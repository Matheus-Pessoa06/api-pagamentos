package com.api_pagamentos.repository;

import com.api_pagamentos.domain.PaymentValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentValueRepository extends JpaRepository<PaymentValue, UUID> {}
