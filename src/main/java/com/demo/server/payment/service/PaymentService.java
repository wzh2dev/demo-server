package com.demo.server.payment.service;

import com.demo.server.payment.domain.Payment;
import com.demo.server.payment.domain.PaymentChannel;
import com.demo.server.payment.domain.PaymentStatus;
import com.demo.server.payment.domain.Refund;
import com.demo.server.payment.domain.RefundStatus;
import com.demo.server.payment.dto.PaymentCreateRequest;
import com.demo.server.payment.dto.RefundRequest;
import com.demo.server.payment.exception.ConflictException;
import com.demo.server.payment.exception.NotFoundException;
import com.demo.server.payment.exception.ValidationException;
import com.demo.server.payment.repository.PaymentRepository;
import com.demo.server.payment.repository.RefundRepository;

import java.util.List;
import java.util.Objects;

/**
 * 支付领域服务。
 *
 * <p>仅用于演示，支付与退款均为内存操作，不接入真实渠道。</p>
 */
public class PaymentService {

    private static final double AMOUNT_EPSILON = 0.0001d;
    private static final int REASON_MAX_LEN = 200;

    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          RefundRepository refundRepository) {
        this.paymentRepository = Objects.requireNonNull(paymentRepository);
        this.refundRepository = Objects.requireNonNull(refundRepository);
    }

    /**
     * 发起支付。
     */
    public Payment create(PaymentCreateRequest req) {
        validateCreate(req);
        if (paymentRepository.findByOrderId(req.getOrderId()) != null) {
            throw new ConflictException("payment already exists for order: " + req.getOrderId());
        }
        // 真实场景应调用订单服务获取金额，这里占位 0
        double amount = 0d;
        Payment payment = new Payment();
        payment.setOrderId(req.getOrderId());
        payment.setAmount(amount);
        payment.setChannel(req.getChannel());
        payment.setSubject(req.getSubject());
        payment.setDetail(req.getDetail());
        payment.setReturnUrl(req.getReturnUrl());
        payment.setStatus(PaymentStatus.PENDING);
        return paymentRepository.save(payment);
    }

    /**
     * 查询支付详情。
     */
    public Payment get(Long id) {
        return requirePayment(id);
    }

    /**
     * 根据订单查询支付。
     */
    public Payment getByOrder(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId);
        if (payment == null) {
            throw new NotFoundException("payment not found for order: " + orderId);
        }
        return payment;
    }

    /**
     * 模拟渠道回调：支付成功。
     */
    public Payment markSuccess(Long id, String tradeNo) {
        Payment payment = requirePayment(id);
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return payment;
        }
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new ConflictException("payment not pending: " + payment.getStatus());
        }
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTradeNo(tradeNo);
        payment.setPaidAt(System.currentTimeMillis());
        return paymentRepository.save(payment);
    }

    /**
     * 模拟渠道回调：支付失败。
     */
    public Payment markFailed(Long id, String reason) {
        Payment payment = requirePayment(id);
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new ConflictException("payment not pending: " + payment.getStatus());
        }
        payment.setStatus(PaymentStatus.FAILED);
        payment.setDetail(reason);
        return paymentRepository.save(payment);
    }

    /**
     * 关闭超时未支付的订单。
     */
    public int closeExpired() {
        long now = System.currentTimeMillis();
        List<Payment> expired = paymentRepository.findExpired(now);
        int n = 0;
        for (Payment p : expired) {
            p.setStatus(PaymentStatus.FAILED);
            p.setDetail("expired");
            paymentRepository.save(p);
            n++;
        }
        return n;
    }

    /**
     * 申请退款。
     */
    public Refund refund(Long paymentId, RefundRequest req) {
        Payment payment = requirePayment(paymentId);
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new ConflictException("payment not success: " + payment.getStatus());
        }
        if (req == null) {
            throw new ValidationException("refund request is null");
        }
        if (req.getAmount() <= 0) {
            throw new ValidationException("refund amount must be > 0");
        }
        if (req.getReason() != null && req.getReason().length() > REASON_MAX_LEN) {
            throw new ValidationException("reason too long");
        }
        double alreadyRefunded = refundRepository.sumRefundedAmountByPayment(paymentId);
        if (req.getAmount() + alreadyRefunded > payment.getAmount() + AMOUNT_EPSILON) {
            throw new ValidationException("refund amount exceeds payment");
        }
        Refund refund = new Refund();
        refund.setPaymentId(paymentId);
        refund.setAmount(req.getAmount());
        refund.setReason(req.getReason());
        refund.setStatus(RefundStatus.PENDING);
        return refundRepository.save(refund);
    }

    /**
     * 标记退款成功。
     */
    public Refund markRefundSuccess(Long refundId) {
        Refund refund = requireRefund(refundId);
        if (refund.getStatus() != RefundStatus.PENDING) {
            throw new ConflictException("refund not pending: " + refund.getStatus());
        }
        refund.setStatus(RefundStatus.SUCCESS);
        refund.setProcessedAt(System.currentTimeMillis());
        Refund saved = refundRepository.save(refund);

        // 若全额退款，更新支付状态
        Payment payment = paymentRepository.findById(refund.getPaymentId());
        if (payment != null) {
            double totalRefunded = refundRepository.sumRefundedAmountByPayment(payment.getId());
            if (totalRefunded >= payment.getAmount() - AMOUNT_EPSILON) {
                payment.setStatus(PaymentStatus.REFUNDED);
                paymentRepository.save(payment);
            }
        }
        return saved;
    }

    /**
     * 标记退款失败。
     */
    public Refund markRefundFailed(Long refundId, String reason) {
        Refund refund = requireRefund(refundId);
        refund.setStatus(RefundStatus.FAILED);
        refund.setReason(reason);
        refund.setProcessedAt(System.currentTimeMillis());
        return refundRepository.save(refund);
    }

    public List<Refund> listRefunds(Long paymentId) {
        return refundRepository.findByPaymentId(paymentId);
    }

    private Payment requirePayment(Long id) {
        Payment payment = paymentRepository.findById(id);
        if (payment == null) {
            throw new NotFoundException("payment", id);
        }
        return payment;
    }

    private Refund requireRefund(Long id) {
        Refund refund = refundRepository.findById(id);
        if (refund == null) {
            throw new NotFoundException("refund", id);
        }
        return refund;
    }

    private void validateCreate(PaymentCreateRequest req) {
        if (req == null) {
            throw new ValidationException("request is null");
        }
        if (req.getOrderId() == null) {
            throw new ValidationException("orderId is null");
        }
        if (req.getChannel() == null) {
            throw new ValidationException("channel is null");
        }
        if (req.getChannel() == PaymentChannel.BALANCE && req.getAmount() < 0) {
            throw new ValidationException("balance amount invalid");
        }
    }
}
