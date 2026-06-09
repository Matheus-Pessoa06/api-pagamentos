package com.api_pagamentos.controller;

import com.api_pagamentos.service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.Invoice;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks")
public class StripeWebhookController {

    private final PaymentService paymentService;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    public StripeWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            System.err.println("-> Falha na verificação da assinatura do Webhook do Stripe: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Assinatura inválida");
        }

        System.out.println("-> Evento de Webhook recebido do Stripe: " + event.getType());

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;

        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            System.err.println("-> Falha ao deserializar o objeto do evento do Stripe");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Falha ao deserializar dados");
        }

        switch (event.getType()) {
            case "invoice.paid":
                Invoice invoice = (Invoice) stripeObject;
                System.out.println("-> Fatura paga recebida: " + invoice.getId());
                
                try {
                    paymentService.markInvoiceAsPaid(invoice.getId());
                } catch (Exception e) {
                    System.err.println("-> Erro ao processar atualização local do pagamento: " + e.getMessage());
                }
                break;

            case "invoice.payment_failed":
                System.out.println("-> Falha no pagamento da fatura: " + ((Invoice) stripeObject).getId());
                break;

            default:
                System.out.println("-> Evento não tratado explicitamente: " + event.getType());
                break;
        }

        return ResponseEntity.ok("Evento recebido com sucesso");
    }
}
