package com.markerhub.common.lang;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Edward
 * @create 2021-04-28 11:53 上午
 */
@Data
public class Result implements Serializable {

    private int code; // 200正常
    private String msg;
    private Object data;

    public static Result succ(Object data) {
        return succ(200, "操作成功", data);
    }

    public static Result succ(int code, String msg, Object data) {
        Result r = new Result();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    public static Result fail(String msg) {
        return fail(400, "失败", null);
    }

    public static Result fail(int code, String msg, Object data) {
        Result r = new Result();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }
}
