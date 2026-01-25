package com.github.churunfa.switchautoweb.vo;

import lombok.Data;

@Data
public class Msg<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> Msg<T> fail(String message) {
        Msg<T> msg = new Msg<>();
        msg.success = false;
        msg.message = message;
        return msg;
    }

    public static <T> Msg<T> success(T data) {
        Msg<T> msg = new Msg<>();
        msg.success = true;
        msg.data = data;
        return msg;
    }
}
