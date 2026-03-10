package id.ac.ui.cs.advprog.eshop.model;

import id.ac.ui.cs.advprog.eshop.enums.PaymentMethod;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
public class Payment {
    private String id;
    private String method;
    private String status;
    private Map<String, String> paymentData;
    private Order order;

    public Payment(String id, String method, Map<String, String> paymentData, Order order) {
        if (id == null || id.isEmpty()) {
            this.id = UUID.randomUUID().toString();
        } else {
            this.id = id;
        }

        if (!PaymentMethod.contains(method)) {
            throw new IllegalArgumentException("Invalid payment method: " + method);
        }

        this.method = method;
        this.paymentData = paymentData;
        this.order = order;
        this.status = PaymentStatus.PENDING.getValue();
    }

    public void setStatus(String status) {
        if (PaymentStatus.contains(status)) {
            this.status = status;
        } else {
            throw new IllegalArgumentException("Invalid payment status: " + status);
        }
    }
}