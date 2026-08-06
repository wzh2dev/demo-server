package com.demo.server.category.exception;

/**
 * 参数校验失败。
 */
public class ValidationException extends BizException {

    public ValidationException(String message) {
        super("INVALID_ARGUMENT", message);
    }
}
