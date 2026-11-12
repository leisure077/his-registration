package com.neuedu.vo;

import com.neuedu.core.ResultCode;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Result {
    // 成功 200  失败 500  未认证401   未权限403
    private Integer code;
    private Object content;
    private String message;
    public Result(ResultCode code, Object content, String message) {
        this.code = code.getValue();
        this.content = content;
        this.message = message;
    }
    public static Result getInstance(ResultCode code, Object content, String message) {
        return new Result(code, content, message);
    }
    public static Result success(Object content, String message) {
        return getInstance(ResultCode.SUCCESS, content, message);
    }
    public static Result success(Object content) {
        return success(content, null);
    }
    public static Result fail(Object content, String message) {
        return getInstance(ResultCode.FAIL, content, message);
    }
    public static Result fail( String message) {
        return fail(null, message);
    }

}
