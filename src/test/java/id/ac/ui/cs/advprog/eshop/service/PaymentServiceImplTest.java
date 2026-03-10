package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.enums.PaymentMethod;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class PaymentServiceImplTest implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Payment addPayment(Order order, String method, Map<String, String> paymentData) {
        String paymentId = UUID.randomUUID().toString();
        Payment payment = new Payment(paymentId, method, paymentData, order);

        String status = determineStatus(method, paymentData);
        payment.setStatus(status);

        paymentRepository.save(payment);
        return payment;
    }

    private String determineStatus(String method, Map<String, String> paymentData) {
        if (PaymentMethod.VOUCHER_CODE.name().equals(method)) {
            return isValidVoucher(paymentData.get("voucherCode"))
                    ? PaymentStatus.SUCCESS.getValue()
                    : PaymentStatus.REJECTED.getValue();
        }
        if (PaymentMethod.BANK_TRANSFER.name().equals(method)) {
            return isValidBankTransfer(paymentData)
                    ? PaymentStatus.SUCCESS.getValue()
                    : PaymentStatus.REJECTED.getValue();
        }
        return PaymentStatus.REJECTED.getValue();
    }

    private boolean isValidVoucher(String voucherCode) {
        if (voucherCode == null) return false;
        if (voucherCode.length() != 16) return false;
        if (!voucherCode.startsWith("ESHOP")) return false;

        long digitCount = voucherCode.chars()
                .filter(Character::isDigit)
                .count();
        return digitCount == 8;
    }

    private boolean isValidBankTransfer(Map<String, String> paymentData) {
        String bankName = paymentData.get("bankName");
        String referenceCode = paymentData.get("referenceCode");
        return bankName != null && !bankName.isEmpty()
                && referenceCode != null && !referenceCode.isEmpty();
    }

    @Override
    public Payment setStatus(Payment payment, String status) {
        payment.setStatus(status);

        if (PaymentStatus.SUCCESS.getValue().equals(status)) {
            payment.getOrder().setStatus(OrderStatus.SUCCESS.getValue());
        } else if (PaymentStatus.REJECTED.getValue().equals(status)) {
            payment.getOrder().setStatus(OrderStatus.FAILED.getValue());
        }

        paymentRepository.save(payment);
        return payment;
    }

    @Override
    public Payment getPayment(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId);
        if (payment == null) {
            throw new NoSuchElementException("Payment not found: " + paymentId);
        }
        return payment;
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}