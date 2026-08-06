package com.demo.server.shipping.exception;

/**
 * 资源不存在。
 */
public class NotFoundException extends BizException {

    public NotFoundException(String resource, Long id) {
        super("NOT_FOUND", resource + " not found: " + id);
    }

    public NotFoundException(String message) {
        super("NOT_FOUND", message);
    }
}
