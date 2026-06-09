package com.api_pagamentos.repository;

import com.api_pagamentos.domain.MonthlyPayment;
import com.api_pagamentos.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MonthlyPaymentRepository extends JpaRepository<MonthlyPayment, UUID> {
    List<MonthlyPayment> findByUser(Users user);
    Optional<MonthlyPayment> findByExternalPaymentId(String externalPaymentId);
}
