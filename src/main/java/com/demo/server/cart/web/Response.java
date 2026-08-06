package com.demo.server.cart.web;

/**
 * 简单响应封装。
 */
public class Response {
    private final int status;
    private final Object body;

    public Response(int status, Object body) {
        this.status = status;
        this.body = body;
    }

    public static Response ok(Object body) {
        return new Response(200, body);
    }

    public static Response created(Object body) {
        return new Response(201, body);
    }

    public static Response noContent() {
        return new Response(204, null);
    }

    public int getStatus() {
        return status;
    }

    public Object getBody() {
        return body;
    }
}
