package com.neuedu.config;

import com.neuedu.vo.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DefaultException {
    @ExceptionHandler
    Result defaultExceptionHandler(Exception ex) {
        ex.printStackTrace();
        return Result.fail("系统异常,请联系管理员");
    }
}
