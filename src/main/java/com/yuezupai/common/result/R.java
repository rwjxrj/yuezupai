package com.yuezupai.common.result;

import lombok.Data;

/**
 * 统一接口返回格式
 */
@Data
public class R<T> {

    private int code;
    private String msg;
    private T data;

    private R() {}

    /** 成功 - 带数据 */
    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.setCode(ResultCode.SUCCESS.getCode());
        r.setMsg(ResultCode.SUCCESS.getMsg());
        r.setData(data);
        return r;
    }

    /** 成功 - 不带数据 */
    public static <T> R<T> ok() {
        return ok(null);
    }

    /** 成功 - 自定义消息 */
    public static <T> R<T> ok(String msg, T data) {
        R<T> r = new R<>();
        r.setCode(ResultCode.SUCCESS.getCode());
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    /** 失败 - 用状态码枚举 */
    public static <T> R<T> fail(ResultCode resultCode) {
        R<T> r = new R<>();
        r.setCode(resultCode.getCode());
        r.setMsg(resultCode.getMsg());
        return r;
    }

    /** 失败 - 自定义消息 */
    public static <T> R<T> fail(int code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    /** 失败 - 默认500 */
    public static <T> R<T> fail(String msg) {
        return fail(ResultCode.ERROR.getCode(), msg);
    }
}