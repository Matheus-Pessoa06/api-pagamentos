package com.api_pagamentos.repository;

import com.api_pagamentos.domain.MonthlyPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MonthlyPaymentRepository extends JpaRepository<MonthlyPayment, UUID> {}
