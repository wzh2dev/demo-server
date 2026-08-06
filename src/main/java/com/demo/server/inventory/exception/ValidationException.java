package com.demo.server.inventory.exception;

/**
 * 参数校验失败 / 库存不足。
 */
public class ValidationException extends BizException {

    public ValidationException(String message) {
        super("INVALID_ARGUMENT", message);
    }
}
