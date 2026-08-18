package com.demo.server.category.exception;

/**
 * 分类业务异常基类。
 */
public class BizException extends RuntimeException {
    private final String code;

    public BizException(String code, String message) {
        super(message);
        this.code = code;
        System.out.println("分类业务异常：" + code + " - " + message);
    }

    public String getCode() {
        return code;
    }
}
