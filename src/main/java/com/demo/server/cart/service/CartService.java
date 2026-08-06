package com.demo.server.cart.service;

import com.demo.server.cart.domain.CartItem;
import com.demo.server.cart.dto.CartAddRequest;
import com.demo.server.cart.dto.CartUpdateRequest;
import com.demo.server.cart.exception.NotFoundException;
import com.demo.server.cart.exception.ValidationException;
import com.demo.server.cart.repository.CartRepository;
import com.demo.server.product.domain.Product;
import com.demo.server.product.repository.ProductRepository;

import java.util.List;
import java.util.Objects;

/**
 * 购物车领域服务。
 */
public class CartService {

    private static final int MAX_QUANTITY = 999;

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = Objects.requireNonNull(cartRepository);
        this.productRepository = Objects.requireNonNull(productRepository);
    }

    /**
     * 加入购物车，已存在则累加数量。
     */
    public CartItem add(Long userId, CartAddRequest req) {
        if (userId == null) {
            throw new ValidationException("userId is null");
        }
        if (req == null) {
            throw new ValidationException("request is null");
        }
        if (req.getProductId() == null) {
            throw new ValidationException("productId is null");
        }
        if (req.getQuantity() < 1) {
            throw new ValidationException("quantity must be >= 1");
        }
        Product product = productRepository.findById(req.getProductId());
        if (product == null) {
            throw new NotFoundException("product", req.getProductId());
        }
        CartItem existing = cartRepository.findByUserAndProduct(userId, req.getProductId());
        if (existing != null) {
            int newQty = existing.getQuantity() + req.getQuantity();
            if (newQty > MAX_QUANTITY) {
                throw new ValidationException("quantity exceeds limit: " + MAX_QUANTITY);
            }
            existing.setQuantity(newQty);
            return cartRepository.save(existing);
        }
        if (req.getQuantity() > MAX_QUANTITY) {
            throw new ValidationException("quantity exceeds limit: " + MAX_QUANTITY);
        }
        CartItem item = new CartItem();
        item.setUserId(userId);
        item.setProductId(req.getProductId());
        item.setQuantity(req.getQuantity());
        item.setSelected(true);
        return cartRepository.save(item);
    }

    /**
     * 更新购物车项数量/选中状态。
     */
    public CartItem update(Long userId, Long itemId, CartUpdateRequest req) {
        if (req == null) {
            throw new ValidationException("request is null");
        }
        CartItem item = requireOwnedItem(userId, itemId);
        if (req.getQuantity() > 0) {
            if (req.getQuantity() > MAX_QUANTITY) {
                throw new ValidationException("quantity exceeds limit: " + MAX_QUANTITY);
            }
            item.setQuantity(req.getQuantity());
        }
        if (req.getSelected() != null) {
            item.setSelected(req.getSelected());
        }
        return cartRepository.save(item);
    }

    public void remove(Long userId, Long itemId) {
        requireOwnedItem(userId, itemId);
        if (!cartRepository.deleteById(itemId)) {
            throw new NotFoundException("cartItem", itemId);
        }
    }

    public void clear(Long userId) {
        cartRepository.deleteByUser(userId);
    }

    /**
     * 清空已选中。
     */
    public int clearSelected(Long userId) {
        return cartRepository.deleteSelectedByUser(userId);
    }

    public List<CartItem> list(Long userId, Boolean selected) {
        return cartRepository.findByUser(userId, selected);
    }

    public int count(Long userId) {
        return cartRepository.countByUser(userId);
    }

    /**
     * 批量设置选中状态。
     */
    public int setAllSelected(Long userId, boolean selected) {
        List<CartItem> items = cartRepository.findByUser(userId, null);
        int n = 0;
        for (CartItem item : items) {
            if (item.isSelected() != selected) {
                item.setSelected(selected);
                cartRepository.save(item);
                n++;
            }
        }
        return n;
    }

    private CartItem requireOwnedItem(Long userId, Long itemId) {
        if (userId == null) {
            throw new ValidationException("userId is null");
        }
        if (itemId == null) {
            throw new ValidationException("itemId is null");
        }
        CartItem item = cartRepository.findById(itemId);
        if (item == null) {
            throw new NotFoundException("cartItem", itemId);
        }
        if (!userId.equals(item.getUserId())) {
            throw new NotFoundException("cartItem", itemId);
        }
        return item;
    }
}
