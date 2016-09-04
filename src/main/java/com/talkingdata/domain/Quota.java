package com.talkingdata.domain;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 封装quota所需要的字段
 * User：    ysl
 * Date:   2016/7/22
 * Time:   10:17
 */
@XmlRootElement
public class Quota {
    private String appkey;
    private String serviceId;
    private long  successRequest;
    private long  outRecords;

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public long getSuccessRequest() {
        return successRequest;
    }

    public void setSuccessRequest(long successRequest) {
        this.successRequest = successRequest;
    }

    public long getOutRecords() {
        return outRecords;
    }

    public void setOutRecords(long outRecords) {
        this.outRecords = outRecords;
    }
}
