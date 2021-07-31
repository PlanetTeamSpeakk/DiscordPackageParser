package com.ptsmods.packageparser.discord.misc;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Subscription {
    private final long id;
    private final int type;
    private final Date currentPeriodStart;
    private final Date currentPeriodEnd;
    private final Integer paymentGateway;
    private final String paymentGateWayPlanId;
    private final String currency;
    private final long planId;
    private final List<Item> items;

    public Subscription(long id, int type, Date currentPeriodStart, Date currentPeriodEnd, Integer paymentGateway, String paymentGateWayPlanId, String currency, long planId, List<Item> items) {
        this.id = id;
        this.type = type;
        this.currentPeriodStart = currentPeriodStart;
        this.currentPeriodEnd = currentPeriodEnd;
        this.paymentGateway = paymentGateway;
        this.paymentGateWayPlanId = paymentGateWayPlanId;
        this.currency = currency;
        this.planId = planId;
        this.items = Collections.unmodifiableList(items);
    }

    public long getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public Date getCurrentPeriodStart() {
        return currentPeriodStart;
    }

    public Date getCurrentPeriodEnd() {
        return currentPeriodEnd;
    }

    public Integer getPaymentGateway() {
        return paymentGateway;
    }

    public String getPaymentGateWayPlanId() {
        return paymentGateWayPlanId;
    }

    public String getCurrency() {
        return currency;
    }

    public long getPlanId() {
        return planId;
    }

    public List<Item> getItems() {
        return items;
    }

    public static class Item {
        private final long id;
        private final long planId;
        private final int quantity;

        public Item(long id, long planId, int quantity) {
            this.id = id;
            this.planId = planId;
            this.quantity = quantity;
        }

        public long getId() {
            return id;
        }

        public long getPlanId() {
            return planId;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
