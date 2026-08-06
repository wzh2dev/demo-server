package com.demo.server.coupon.exception;

/**
 * 参数校验失败。
 */
public class ValidationException extends BizException {

    public ValidationException(String message) {
        super("INVALID_ARGUMENT", message);
    }
}
