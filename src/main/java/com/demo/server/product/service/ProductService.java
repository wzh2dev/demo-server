package com.demo.server.product.service;

import com.demo.server.product.domain.Product;
import com.demo.server.product.domain.ProductStatus;
import com.demo.server.product.dto.PageResult;
import com.demo.server.product.dto.ProductCreateRequest;
import com.demo.server.product.dto.ProductUpdateRequest;
import com.demo.server.product.exception.NotFoundException;
import com.demo.server.product.exception.ValidationException;
import com.demo.server.product.repository.ProductRepository;

import java.util.List;
import java.util.Objects;

/**
 * 商品领域服务。
 */
public class ProductService {

    private static final int NAME_MAX_LEN = 128;
    private static final int DESCRIPTION_MAX_LEN = 2000;

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = Objects.requireNonNull(productRepository);
    }

    public Product create(ProductCreateRequest req) {
        validateCreate(req);
        Product product = new Product();
        product.setName(req.getName());
        product.setSubtitle(req.getSubtitle());
        product.setDescription(req.getDescription());
        product.setCategoryId(req.getCategoryId());
        product.setBrand(req.getBrand());
        product.setPrice(req.getPrice());
        product.setOriginalPrice(req.getOriginalPrice());
        if (req.getImages() != null) {
            product.setImages(req.getImages());
        }
        product.setStatus(ProductStatus.ON_SALE);
        return productRepository.save(product);
    }

    public Product update(Long id, ProductUpdateRequest req) {
        Product product = requireProduct(id);
        if (req.getName() != null) {
            if (req.getName().length() > NAME_MAX_LEN) {
                throw new ValidationException("name too long");
            }
            product.setName(req.getName());
        }
        if (req.getSubtitle() != null) {
            product.setSubtitle(req.getSubtitle());
        }
        if (req.getDescription() != null) {
            if (req.getDescription().length() > DESCRIPTION_MAX_LEN) {
                throw new ValidationException("description too long");
            }
            product.setDescription(req.getDescription());
        }
        if (req.getPrice() != null) {
            if (req.getPrice() < 0) {
                throw new ValidationException("price must be >= 0");
            }
            product.setPrice(req.getPrice());
        }
        if (req.getStatus() != null) {
            product.setStatus(req.getStatus());
        }
        return productRepository.save(product);
    }

    public Product get(Long id) {
        return requireProduct(id);
    }

    public void delete(Long id) {
        if (!productRepository.deleteById(id)) {
            throw new NotFoundException("product", id);
        }
    }

    public PageResult<Product> list(Long categoryId, Double minPrice, Double maxPrice,
                                    String sort, int pageNumber, int pageSize) {
        if (pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageSize < 1 || pageSize > 100) {
            pageSize = 20;
        }
        List<Product> items = productRepository.findAll(categoryId, minPrice, maxPrice, sort, pageNumber, pageSize);
        long total = productRepository.count(categoryId, minPrice, maxPrice);
        return new PageResult<>(items, total, pageNumber, pageSize);
    }

    public List<Product> listOnSale() {
        return productRepository.findByStatus(ProductStatus.ON_SALE);
    }

    private Product requireProduct(Long id) {
        Product product = productRepository.findById(id);
        if (product == null) {
            throw new NotFoundException("product", id);
        }
        return product;
    }

    private void validateCreate(ProductCreateRequest req) {
        if (req == null) {
            throw new ValidationException("request is null");
        }
        if (req.getName() == null || req.getName().isEmpty()) {
            throw new ValidationException("name is empty");
        }
        if (req.getName().length() > NAME_MAX_LEN) {
            throw new ValidationException("name too long");
        }
        if (req.getCategoryId() == null) {
            throw new ValidationException("categoryId is null");
        }
        if (req.getPrice() < 0) {
            throw new ValidationException("price must be >= 0");
        }
        if (req.getOriginalPrice() != null && req.getOriginalPrice() < 0) {
            throw new ValidationException("originalPrice must be >= 0");
        }
    }
}
