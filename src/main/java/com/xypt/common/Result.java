package com.xypt.common;

import lombok.Data;

/**
 * 统一API返回结果封装类
 * 所有接口都应返回此格式，保证前后端数据格式统一
 */
@Data
public class Result<T> {

    /** 状态码：200=成功，400=参数错误，401=未登录，403=无权限，500=服务器错误 */
    private Integer code;

    /** 返回消息 */
    private String message;

    /** 返回数据 */
    private T data;

    /**
     * 成功（带数据）
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 成功（不带数据）
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        return result;
    }

    /**
     * 成功（带自定义消息）
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 失败
     */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    /**
     * 失败（带状态码）
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 未登录
     */
    public static <T> Result<T> unauthorized() {
        return error(401, "请先登录");
    }

    /**
     * 无权限
     */
    public static <T> Result<T> forbidden() {
        return error(403, "无操作权限");
    }
}
