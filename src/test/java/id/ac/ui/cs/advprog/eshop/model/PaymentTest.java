package id.ac.ui.cs.advprog.eshop.model;

import id.ac.ui.cs.advprog.eshop.enums.PaymentMethod;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    private Order order;
    private Map<String, String> paymentData;

    @BeforeEach
    void setUp() {
        Product product = new Product();
        product.setProductName("Laptop");
        product.setProductQuantity(1);

        List<Product> products = new ArrayList<>();
        products.add(product);

        order = new Order("order-1", products, 1708560000L, "author1");
        paymentData = new HashMap<>();
    }

    @Test
    void testCreatePaymentWithValidVoucherMethod() {
        paymentData.put("voucherCode", "ESHOP1234ABC5678");
        Payment payment = new Payment("pay-1", PaymentMethod.VOUCHER_CODE.name(), paymentData, order);

        assertEquals("pay-1", payment.getId());
        assertEquals(PaymentMethod.VOUCHER_CODE.name(), payment.getMethod());
        assertEquals(PaymentStatus.PENDING.getValue(), payment.getStatus());
        assertEquals(order, payment.getOrder());
    }

    @Test
    void testCreatePaymentWithValidBankTransferMethod() {
        paymentData.put("bankName", "BCA");
        paymentData.put("referenceCode", "REF123");
        Payment payment = new Payment("pay-2", PaymentMethod.BANK_TRANSFER.name(), paymentData, order);

        assertEquals(PaymentMethod.BANK_TRANSFER.name(), payment.getMethod());
        assertEquals(PaymentStatus.PENDING.getValue(), payment.getStatus());
    }

    @Test
    void testCreatePaymentWithInvalidMethod() {
        assertThrows(IllegalArgumentException.class, () ->
                new Payment("pay-3", "INVALID_METHOD", paymentData, order));
    }

    @Test
    void testCreatePaymentAutoGeneratesIdWhenEmpty() {
        paymentData.put("voucherCode", "ESHOP1234ABC5678");
        Payment payment = new Payment("", PaymentMethod.VOUCHER_CODE.name(), paymentData, order);

        assertNotNull(payment.getId());
        assertFalse(payment.getId().isEmpty());
    }

    @Test
    void testSetStatusSuccess() {
        paymentData.put("voucherCode", "ESHOP1234ABC5678");
        Payment payment = new Payment("pay-1", PaymentMethod.VOUCHER_CODE.name(), paymentData, order);
        payment.setStatus(PaymentStatus.SUCCESS.getValue());

        assertEquals(PaymentStatus.SUCCESS.getValue(), payment.getStatus());
    }

    @Test
    void testSetStatusRejected() {
        paymentData.put("voucherCode", "ESHOP1234ABC5678");
        Payment payment = new Payment("pay-1", PaymentMethod.VOUCHER_CODE.name(), paymentData, order);
        payment.setStatus(PaymentStatus.REJECTED.getValue());

        assertEquals(PaymentStatus.REJECTED.getValue(), payment.getStatus());
    }

    @Test
    void testSetStatusInvalidThrowsException() {
        paymentData.put("voucherCode", "ESHOP1234ABC5678");
        Payment payment = new Payment("pay-1", PaymentMethod.VOUCHER_CODE.name(), paymentData, order);

        assertThrows(IllegalArgumentException.class, () ->
                payment.setStatus("INVALID_STATUS"));
    }
}