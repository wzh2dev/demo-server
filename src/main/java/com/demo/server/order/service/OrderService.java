package com.demo.server.order.service;

import com.demo.server.order.domain.Order;
import com.demo.server.order.domain.OrderItem;
import com.demo.server.order.domain.OrderStatus;
import com.demo.server.order.dto.OrderCreateRequest;
import com.demo.server.order.dto.OrderUpdateRequest;
import com.demo.server.order.dto.PageResult;
import com.demo.server.order.exception.ConflictException;
import com.demo.server.order.exception.NotFoundException;
import com.demo.server.order.exception.ValidationException;
import com.demo.server.order.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 订单领域服务。
 */
public class OrderService {

    private static final int REMARK_MAX_LEN = 200;

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = Objects.requireNonNull(orderRepository);
    }

    /**
     * 创建订单。
     */
    public Order create(Long userId, OrderCreateRequest req) {
        validateCreate(userId, req);
        Order order = new Order();
        order.setUserId(userId);
        order.setAddressId(req.getAddressId());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setRemark(req.getRemark());
        double total = 0d;
        for (OrderCreateRequest.Item it : req.getItems()) {
            OrderItem item = new OrderItem();
            item.setProductId(it.getProductId());
            item.setSkuId(it.getSkuId());
            item.setQuantity(it.getQuantity());
            // 真实场景应查 product/sku 服务取价
            item.setPrice(0d);
            item.setSubtotal(0d);
            order.getItems().add(item);
            total += item.getSubtotal();
        }
        order.setTotalAmount(total);
        order.setPayAmount(total);
        return orderRepository.save(order);
    }

    /**
     * 更新订单。
     */
    public Order update(Long id, OrderUpdateRequest req) {
        Order order = requireOrder(id);
        if (req.getStatus() != null) {
            ensureTransition(order.getStatus(), req.getStatus());
            order.setStatus(req.getStatus());
            switch (req.getStatus()) {
                case PAID:
                    order.setPaidAt(System.currentTimeMillis());
                    break;
                case SHIPPED:
                    order.setShippedAt(System.currentTimeMillis());
                    break;
                case COMPLETED:
                    order.setCompletedAt(System.currentTimeMillis());
                    break;
                default:
                    break;
            }
        }
        if (req.getRemark() != null) {
            if (req.getRemark().length() > REMARK_MAX_LEN) {
                throw new ValidationException("remark too long");
            }
            order.setRemark(req.getRemark());
        }
        return orderRepository.save(order);
    }

    /**
     * 取消订单。
     */
    public Order cancel(Long id) {
        Order order = requireOrder(id);
        if (order.getStatus() == OrderStatus.COMPLETED
                || order.getStatus() == OrderStatus.CANCELLED
                || order.getStatus() == OrderStatus.REFUNDED) {
            throw new ConflictException("order cannot be cancelled: " + order.getStatus());
        }
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    public Order get(Long id) {
        return requireOrder(id);
    }

    public PageResult<Order> list(Long userId, OrderStatus status, int pageNumber, int pageSize) {
        if (pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageSize < 1 || pageSize > 100) {
            pageSize = 20;
        }
        List<Order> items = orderRepository.findAll(userId, status, pageNumber, pageSize);
        long total = orderRepository.count(userId, status);
        return new PageResult<>(items, total, pageNumber, pageSize);
    }

    public List<Order> listByUser(Long userId) {
        return new ArrayList<>(orderRepository.findByUserId(userId));
    }

    private Order requireOrder(Long id) {
        Order order = orderRepository.findById(id);
        if (order == null) {
            throw new NotFoundException("order", id);
        }
        return order;
    }

    private void validateCreate(Long userId, OrderCreateRequest req) {
        if (userId == null) {
            throw new ValidationException("userId is null");
        }
        if (req == null) {
            throw new ValidationException("request is null");
        }
        if (req.getAddressId() == null) {
            throw new ValidationException("addressId is null");
        }
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new ValidationException("items is empty");
        }
        for (OrderCreateRequest.Item it : req.getItems()) {
            if (it.getQuantity() == null || it.getQuantity() < 1) {
                throw new ValidationException("invalid quantity");
            }
        }
        if (req.getRemark() != null && req.getRemark().length() > REMARK_MAX_LEN) {
            throw new ValidationException("remark too long");
        }
    }

    private void ensureTransition(OrderStatus from, OrderStatus to) {
        boolean valid;
        switch (from) {
            case PENDING_PAYMENT:
                valid = to == OrderStatus.PAID || to == OrderStatus.CANCELLED;
                break;
            case PAID:
                valid = to == OrderStatus.SHIPPED || to == OrderStatus.REFUNDED
                        || to == OrderStatus.CANCELLED;
                break;
            case SHIPPED:
                valid = to == OrderStatus.COMPLETED;
                break;
            default:
                valid = false;
        }
        if (!valid) {
            throw new ConflictException("invalid status transition: " + from + " -> " + to);
        }
    }
}
