package com.demo.server.coupon.exception;

/**
 * 资源冲突。
 */
public class ConflictException extends BizException {

    public ConflictException(String message) {
        super("CONFLICT", message);
    }
}
