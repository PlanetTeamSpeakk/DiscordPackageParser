package com.ptsmods.packageparser.discord.misc;

import java.util.Date;
import java.util.List;

public class Payment {
    private final long id;
    private final Date createdAt;
    private final String currency;
    private final int tax;
    private final boolean taxInclusive;
    private final int amount;
    private final int amountRefunded;
    private final int status;
    private final String description;
    private final int flags;
    private final Subscription subscription;
    private final PaymentSource paymentSource;
    private final long skuId;
    private final int skuPrice;
    private final long skuSubscriptionPlanId;

    public Payment(long id, Date createdAt, String currency, int tax, boolean taxInclusive, int amount, int amountRefunded, int status, String description, int flags, Subscription subscription, PaymentSource paymentSource, long skuId, int skuPrice, long skuSubscriptionPlanId) {
        this.id = id;
        this.createdAt = createdAt;
        this.currency = currency;
        this.tax = tax;
        this.taxInclusive = taxInclusive;
        this.amount = amount;
        this.amountRefunded = amountRefunded;
        this.status = status;
        this.description = description;
        this.flags = flags;
        this.subscription = subscription;
        this.paymentSource = paymentSource;
        this.skuId = skuId;
        this.skuPrice = skuPrice;
        this.skuSubscriptionPlanId = skuSubscriptionPlanId;
    }

    public long getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getCurrency() {
        return currency;
    }

    public int getTax() {
        return tax;
    }

    public boolean isTaxInclusive() {
        return taxInclusive;
    }

    public int getAmount() {
        return amount;
    }

    public int getAmountRefunded() {
        return amountRefunded;
    }

    public int getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public int getFlags() {
        return flags;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public PaymentSource getPaymentSource() {
        return paymentSource;
    }

    public long getSkuId() {
        return skuId;
    }

    public int getSkuPrice() {
        return skuPrice;
    }

    public long getSkuSubscriptionPlanId() {
        return skuSubscriptionPlanId;
    }

    public static class PaymentSource {
        private final long id;
        private final int type;
        private final boolean invalid;
        private final String email;
        private final Address billingAddress;
        private final String country;

        public PaymentSource(long id, int type, boolean invalid, String email, Address billingAddress, String country) {
            this.id = id;
            this.type = type;
            this.invalid = invalid;
            this.email = email;
            this.billingAddress = billingAddress;
            this.country = country;
        }

        public long getId() {
            return id;
        }

        public int getType() {
            return type;
        }

        public boolean isInvalid() {
            return invalid;
        }

        public String getEmail() {
            return email;
        }

        public Address getBillingAddress() {
            return billingAddress;
        }

        public String getCountry() {
            return country;
        }
    }
}
