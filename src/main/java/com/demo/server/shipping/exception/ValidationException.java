package com.demo.server.shipping.exception;

/**
 * 参数校验失败 / 状态非法。
 */
public class ValidationException extends BizException {

    public ValidationException(String message) {
        super("INVALID_ARGUMENT", message);
    }
}
