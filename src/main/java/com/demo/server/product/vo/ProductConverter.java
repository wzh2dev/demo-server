package com.demo.server.product.vo;

import com.demo.server.product.domain.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品 Entity ↔ VO 转换器。
 */
public final class ProductConverter {

    private ProductConverter() {
    }

    public static ProductVO toVO(Product product) {
        if (product == null) {
            return null;
        }
        ProductVO vo = new ProductVO();
        vo.setId(product.getId());
        vo.setName(product.getName());
        vo.setSubtitle(product.getSubtitle());
        vo.setDescription(product.getDescription());
        vo.setCategoryId(product.getCategoryId());
        vo.setBrand(product.getBrand());
        vo.setPrice(product.getPrice());
        vo.setOriginalPrice(product.getOriginalPrice());
        vo.setImages(product.getImages());
        vo.setStatus(product.getStatus() == null ? null : product.getStatus().name());
        vo.setStock(product.getStock());
        vo.setSales(product.getSales());
        vo.setDiscountRate(computeDiscountRate(product));
        vo.setCreatedAt(product.getCreatedAt());
        vo.setUpdatedAt(product.getUpdatedAt());
        return vo;
    }

    public static List<ProductVO> toVOList(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return new ArrayList<>();
        }
        List<ProductVO> list = new ArrayList<>(products.size());
        for (Product product : products) {
            list.add(toVO(product));
        }
        return list;
    }

    /**
     * 计算折扣率（0-100，保留 1 位小数）。
     * 例如：原价 100，现价 80，返回 80。
     */
    private static double computeDiscountRate(Product product) {
        if (product.getOriginalPrice() <= 0) {
            return 0d;
        }
        double rate = product.getPrice() / product.getOriginalPrice() * 100d;
        return Math.round(rate * 10d) / 10d;
    }
}
