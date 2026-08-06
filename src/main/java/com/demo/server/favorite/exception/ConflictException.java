package com.demo.server.favorite.exception;

/**
 * 资源冲突。
 */
public class ConflictException extends BizException {

    public ConflictException(String message) {
        super("CONFLICT", message);
    }
}
