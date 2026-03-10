package id.ac.ui.cs.advprog.eshop.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    VOUCHER_CODE("VOUCHER_CODE"),
    BANK_TRANSFER("BANK_TRANSFER");

    private final String value;

    PaymentMethod(String value) {
        this.value = value;
    }

    public static boolean contains(String param) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.name().equals(param)) {
                return true;
            }
        }
        return false;
    }
}