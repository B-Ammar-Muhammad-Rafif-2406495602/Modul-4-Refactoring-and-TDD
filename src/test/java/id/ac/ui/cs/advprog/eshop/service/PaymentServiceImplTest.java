package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.enums.PaymentMethod;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @InjectMocks
    PaymentServiceImpl paymentService;

    @Mock
    PaymentRepository paymentRepository;

    Order order;
    List<Product> products;

    @BeforeEach
    void setUp() {
        Product product = new Product();
        product.setProductName("Laptop");
        product.setProductQuantity(1);

        products = new ArrayList<>();
        products.add(product);

        order = new Order("order-1", products, 1708560000L, "author1");
    }

    // ===================== addPayment - Voucher Code =====================

    @Test
    void testAddPaymentVoucherCodeValid() {
        Map<String, String> data = new HashMap<>();
        data.put("voucherCode", "ESHOP1234ABC5678");

        doReturn(null).when(paymentRepository).save(any(Payment.class));

        Payment payment = paymentService.addPayment(order, PaymentMethod.VOUCHER_CODE.name(), data);

        assertEquals(PaymentStatus.SUCCESS.getValue(), payment.getStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testAddPaymentVoucherCodeNot16Chars() {
        Map<String, String> data = new HashMap<>();
        data.put("voucherCode", "ESHOP123");

        doReturn(null).when(paymentRepository).save(any(Payment.class));

        Payment payment = paymentService.addPayment(order, PaymentMethod.VOUCHER_CODE.name(), data);
        assertEquals(PaymentStatus.REJECTED.getValue(), payment.getStatus());
    }

    @Test
    void testAddPaymentVoucherCodeNotStartWithESHOP() {
        Map<String, String> data = new HashMap<>();
        data.put("voucherCode", "ABCDE1234ABC5678");

        doReturn(null).when(paymentRepository).save(any(Payment.class));

        Payment payment = paymentService.addPayment(order, PaymentMethod.VOUCHER_CODE.name(), data);
        assertEquals(PaymentStatus.REJECTED.getValue(), payment.getStatus());
    }

    @Test
    void testAddPaymentVoucherCodeNot8Digits() {
        Map<String, String> data = new HashMap<>();
        // Only 4 digits (1234), rest are letters
        data.put("voucherCode", "ESHOP1234ABCDEFG");

        doReturn(null).when(paymentRepository).save(any(Payment.class));

        Payment payment = paymentService.addPayment(order, PaymentMethod.VOUCHER_CODE.name(), data);
        assertEquals(PaymentStatus.REJECTED.getValue(), payment.getStatus());
    }

    @Test
    void testAddPaymentVoucherCodeNull() {
        Map<String, String> data = new HashMap<>();
        data.put("voucherCode", null);

        doReturn(null).when(paymentRepository).save(any(Payment.class));

        Payment payment = paymentService.addPayment(order, PaymentMethod.VOUCHER_CODE.name(), data);
        assertEquals(PaymentStatus.REJECTED.getValue(), payment.getStatus());
    }

    // ===================== addPayment - Bank Transfer =====================

    @Test
    void testAddPaymentBankTransferValid() {
        Map<String, String> data = new HashMap<>();
        data.put("bankName", "BCA");
        data.put("referenceCode", "REF123456");

        doReturn(null).when(paymentRepository).save(any(Payment.class));

        Payment payment = paymentService.addPayment(order, PaymentMethod.BANK_TRANSFER.name(), data);
        assertEquals(PaymentStatus.SUCCESS.getValue(), payment.getStatus());
    }

    @Test
    void testAddPaymentBankTransferEmptyBankName() {
        Map<String, String> data = new HashMap<>();
        data.put("bankName", "");
        data.put("referenceCode", "REF123456");

        doReturn(null).when(paymentRepository).save(any(Payment.class));

        Payment payment = paymentService.addPayment(order, PaymentMethod.BANK_TRANSFER.name(), data);
        assertEquals(PaymentStatus.REJECTED.getValue(), payment.getStatus());
    }

    @Test
    void testAddPaymentBankTransferNullReferenceCode() {
        Map<String, String> data = new HashMap<>();
        data.put("bankName", "BCA");
        data.put("referenceCode", null);

        doReturn(null).when(paymentRepository).save(any(Payment.class));

        Payment payment = paymentService.addPayment(order, PaymentMethod.BANK_TRANSFER.name(), data);
        assertEquals(PaymentStatus.REJECTED.getValue(), payment.getStatus());
    }

    // ===================== setStatus =====================

    @Test
    void testSetStatusSuccessAlsoSetsOrderSuccess() {
        Map<String, String> data = new HashMap<>();
        data.put("voucherCode", "ESHOP1234ABC5678");
        Payment payment = new Payment("pay-1", PaymentMethod.VOUCHER_CODE.name(), data, order);

        doReturn(payment).when(paymentRepository).save(any(Payment.class));

        paymentService.setStatus(payment, PaymentStatus.SUCCESS.getValue());

        assertEquals(PaymentStatus.SUCCESS.getValue(), payment.getStatus());
        assertEquals(OrderStatus.SUCCESS.getValue(), order.getStatus());
    }

    @Test
    void testSetStatusRejectedAlsoSetsOrderFailed() {
        Map<String, String> data = new HashMap<>();
        data.put("voucherCode", "ESHOP1234ABC5678");
        Payment payment = new Payment("pay-1", PaymentMethod.VOUCHER_CODE.name(), data, order);

        doReturn(payment).when(paymentRepository).save(any(Payment.class));

        paymentService.setStatus(payment, PaymentStatus.REJECTED.getValue());

        assertEquals(PaymentStatus.REJECTED.getValue(), payment.getStatus());
        assertEquals(OrderStatus.FAILED.getValue(), order.getStatus());
    }

    @Test
    void testSetStatusInvalidThrowsException() {
        Map<String, String> data = new HashMap<>();
        data.put("voucherCode", "ESHOP1234ABC5678");
        Payment payment = new Payment("pay-1", PaymentMethod.VOUCHER_CODE.name(), data, order);

        assertThrows(IllegalArgumentException.class, () ->
                paymentService.setStatus(payment, "INVALID_STATUS"));
    }

    // ===================== getPayment =====================

    @Test
    void testGetPaymentWithValidId() {
        Map<String, String> data = new HashMap<>();
        data.put("voucherCode", "ESHOP1234ABC5678");
        Payment payment = new Payment("pay-1", PaymentMethod.VOUCHER_CODE.name(), data, order);

        doReturn(payment).when(paymentRepository).findById("pay-1");

        Payment found = paymentService.getPayment("pay-1");
        assertEquals("pay-1", found.getId());
    }

    @Test
    void testGetPaymentWithInvalidIdThrowsException() {
        doReturn(null).when(paymentRepository).findById("non-existent");

        assertThrows(NoSuchElementException.class, () ->
                paymentService.getPayment("non-existent"));
    }

    // ===================== getAllPayments =====================

    @Test
    void testGetAllPaymentsReturnsAll() {
        Map<String, String> data = new HashMap<>();
        data.put("voucherCode", "ESHOP1234ABC5678");
        Payment pay1 = new Payment("pay-1", PaymentMethod.VOUCHER_CODE.name(), data, order);
        Payment pay2 = new Payment("pay-2", PaymentMethod.VOUCHER_CODE.name(), data, order);

        doReturn(List.of(pay1, pay2)).when(paymentRepository).findAll();

        List<Payment> all = paymentService.getAllPayments();
        assertEquals(2, all.size());
    }
}