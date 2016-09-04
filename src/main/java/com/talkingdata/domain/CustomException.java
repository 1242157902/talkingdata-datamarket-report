package com.talkingdata.domain;

/**
 * 自定义异常
 * User：    ysl
 * Date:   2016/7/14
 * Time:   13:43
 */
public class CustomException {
    private String code ;
    private String message ;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
