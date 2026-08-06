package com.demo.server.search.exception;

/**
 * 搜索业务异常基类。
 */
public class BizException extends RuntimeException {
    private final String code;

    public BizException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
