package com.demo.server.product.exception;

/**
 * 资源不存在。
 */
public class NotFoundException extends BizException {

    public NotFoundException(String resource, Long id) {
        super("NOT_FOUND", resource + " not found: " + id);
    }
}
