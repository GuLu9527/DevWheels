package com.dw.common.result;

import lombok.Data;

/**
 * 统一响应体
 * @param <T> 响应数据类型
 */
@Data
public class DWResult<T> {

    /**
     * 状态码：200-成功，500-失败，400-参数错误，401-未登录，403-无权限
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 成功响应（带数据）
     */
    public static <T> DWResult<T> success(T data) {
        DWResult<T> result = new DWResult<>();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 成功响应（无数据）
     */
    public static DWResult<?> success() {
        return success(null);
    }

    /**
     * 失败响应
     */
    public static <T> DWResult<T> fail(String msg) {
        DWResult<T> result = new DWResult<>();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }

    /**
     * 自定义状态码的失败响应
     */
    public static <T> DWResult<T> fail(int code, String msg) {
        DWResult<T> result = new DWResult<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
