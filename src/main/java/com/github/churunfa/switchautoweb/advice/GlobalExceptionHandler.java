package com.github.churunfa.switchautoweb.advice;

import com.github.churunfa.switchautoweb.vo.Msg;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Msg<String> handleBusinessException(Exception e) {
        return Msg.fail(e.getMessage());
    }

}