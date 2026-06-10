package com.api_pagamentos.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Invoice;
import com.stripe.model.InvoiceItem;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.InvoiceCreateParams;
import com.stripe.param.InvoiceItemCreateParams;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService {

    public String createCustomer(String name, String email, String phone) throws StripeException {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setName(name)
                .setEmail(email)
                .setPhone(phone)
                .build();

        Customer customer = Customer.create(params);
        return customer.getId();
    }

    public String createProductAndPrice(String productName, Double value) throws StripeException {

        ProductCreateParams productParams = ProductCreateParams.builder()
                .setName(productName)
                .build();
        Product product = Product.create(productParams);


        long amountInCents = Math.round(value * 100);


        PriceCreateParams priceParams = PriceCreateParams.builder()
                .setProduct(product.getId())
                .setUnitAmount(amountInCents)
                .setCurrency("brl")
                .build();
        Price price = Price.create(priceParams);

        return price.getId();
    }

    public Map<String, String> createInvoice(String stripeCustomerId, String stripePriceId, LocalDateTime dueDate) throws StripeException {
        long dueDateEpoch = dueDate.atZone(ZoneId.systemDefault()).toEpochSecond();

        InvoiceCreateParams invoiceParams = InvoiceCreateParams.builder()
                .setCustomer(stripeCustomerId)
                .setCollectionMethod(InvoiceCreateParams.CollectionMethod.SEND_INVOICE)
                .setDueDate(dueDateEpoch)
                .build();
        Invoice invoice = Invoice.create(invoiceParams);

        InvoiceItemCreateParams itemParams = InvoiceItemCreateParams.builder()
                .setCustomer(stripeCustomerId)
                .setPrice(stripePriceId)
                .setInvoice(invoice.getId())
                .build();
        InvoiceItem.create(itemParams);

        Invoice finalizedInvoice = invoice.finalizeInvoice();

        Map<String, String> result = new HashMap<>();
        result.put("invoiceId", finalizedInvoice.getId());
        result.put("paymentUrl", finalizedInvoice.getHostedInvoiceUrl());
        return result;
    }
}
