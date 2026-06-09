package com.api_pagamentos.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name= "monthly_payment")


public class MonthlyPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Boolean paid;
    private LocalDateTime paidAt;
    private Double value;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private String externalPaymentId;
    private String paymentMethodType;
    private String paymentUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }

    public MonthlyPayment(){}
}
