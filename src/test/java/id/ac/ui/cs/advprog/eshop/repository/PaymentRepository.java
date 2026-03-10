package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.enums.PaymentMethod;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Repository
class PaymentRepositoryTest {

    PaymentRepository paymentRepository;
    Order order;

    @BeforeEach
    void setUp() {
        paymentRepository = new PaymentRepository();

        Product product = new Product();
        product.setProductName("Book");
        product.setProductQuantity(2);

        List<Product> products = new ArrayList<>();
        products.add(product);

        order = new Order("order-1", products, 1708560000L, "author1");
    }

    @Test
    void testSaveNewPayment() {
        Map<String, String> data = new HashMap<>();
        data.put("voucherCode", "ESHOP1234ABC5678");
        Payment payment = new Payment("pay-1", PaymentMethod.VOUCHER_CODE.name(), data, order);

        Payment saved = paymentRepository.save(payment);

        assertNotNull(saved);
        assertEquals("pay-1", saved.getId());
    }

    @Test
    void testSaveUpdatesExistingPayment() {
        Map<String, String> data = new HashMap<>();
        data.put("voucherCode", "ESHOP1234ABC5678");
        Payment payment = new Payment("pay-1", PaymentMethod.VOUCHER_CODE.name(), data, order);
        paymentRepository.save(payment);

        // Update status
        payment.setStatus("SUCCESS");
        paymentRepository.save(payment);

        Payment found = paymentRepository.findById("pay-1");
        assertEquals("SUCCESS", found.getStatus());
        assertEquals(1, paymentRepository.findAll().size());
    }

    @Test
    void testFindByIdWithValidId() {
        Map<String, String> data = new HashMap<>();
        data.put("voucherCode", "ESHOP1234ABC5678");
        Payment payment = new Payment("pay-1", PaymentMethod.VOUCHER_CODE.name(), data, order);
        paymentRepository.save(payment);

        Payment found = paymentRepository.findById("pay-1");

        assertNotNull(found);
        assertEquals("pay-1", found.getId());
    }

    @Test
    void testFindByIdWithInvalidId() {
        Payment found = paymentRepository.findById("non-existent-id");
        assertNull(found);
    }

    @Test
    void testFindAllReturnsAllPayments() {
        Map<String, String> data1 = new HashMap<>();
        data1.put("voucherCode", "ESHOP1234ABC5678");
        Payment pay1 = new Payment("pay-1", PaymentMethod.VOUCHER_CODE.name(), data1, order);

        Map<String, String> data2 = new HashMap<>();
        data2.put("bankName", "BCA");
        data2.put("referenceCode", "REF123");
        Payment pay2 = new Payment("pay-2", PaymentMethod.BANK_TRANSFER.name(), data2, order);

        paymentRepository.save(pay1);
        paymentRepository.save(pay2);

        List<Payment> all = paymentRepository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void testFindAllReturnsEmptyWhenNoPayments() {
        List<Payment> all = paymentRepository.findAll();
        assertTrue(all.isEmpty());
    }
}