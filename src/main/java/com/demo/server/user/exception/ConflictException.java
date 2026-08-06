package com.demo.server.user.exception;

/**
 * 资源冲突。
 */
public class ConflictException extends BizException {

    public ConflictException(String message) {
        super("CONFLICT", message);
    }
}
