package com.talkingdata.domain;

/**
 * 鉴权日志的访问次数，成功次数，失败次数
 * User：    ysl
 * Date:   2016/8/12
 * Time:   12:05
 */
public class Shrio {


    private long  accessRequest;             //访问次数
    private long successRequest;            //成功调用次数

    public long getAccessRequest() {
        return accessRequest;
    }

    public void setAccessRequest(long accessRequest) {
        this.accessRequest = accessRequest;
    }

    public long getSuccessRequest() {
        return successRequest;
    }

    public void setSuccessRequest(long successRequest) {
        this.successRequest = successRequest;
    }

    @Override
    public String toString() {
        return "Shrio{" +
                "accessRequest=" + accessRequest +
                ", successRequest=" + successRequest +
                '}';
    }
}
