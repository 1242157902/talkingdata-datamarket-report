package com.talkingdata.view;

/**
 * User：    ysl
 * Date:   2016/8/5
 * Time:   12:05
 */
public class Status {
    private int status;                 //请求返回的状态码
    private String msg;                 //代表的含义

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
