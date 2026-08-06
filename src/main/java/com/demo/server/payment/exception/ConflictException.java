package com.demo.server.payment.exception;

/**
 * 资源冲突 / 状态非法。
 */
public class ConflictException extends BizException {

    public ConflictException(String message) {
        super("CONFLICT", message);
    }
}
