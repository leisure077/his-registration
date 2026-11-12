package com.neuedu.core;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(200),
    FAIL(500),
    UNAUTHORIZED(401),
    FORBIDDEN(403);
    private Integer value;

    ResultCode(Integer value) {
        this.value = value;
    }
}
