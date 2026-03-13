package com.yuezupai.common.exception;

import com.yuezupai.common.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常（手动抛出，会被全局捕获并返回给前端）
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.ERROR.getCode();
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.code = resultCode.getCode();
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }
}