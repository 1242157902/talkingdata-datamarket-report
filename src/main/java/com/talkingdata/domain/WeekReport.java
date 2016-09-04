package com.talkingdata.domain;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * User：    ysl
 * Date:   2016/7/28
 * Time:   18:16
 */
@XmlRootElement
public class WeekReport {

    private String serviceId;                 //服务id
    private long  accessRequest;             //访问次数
    private long successRequest;            //成功调用次数
    private long inputCount;                //输入记录
    private long oututCount;                //输出记录


    public long getAccessRequest() {
        return accessRequest;
    }

    public void setAccessRequest(long accessRequest) {
        this.accessRequest = accessRequest;
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

    public long getInputCount() {
        return inputCount;
    }

    public void setInputCount(long inputCount) {
        this.inputCount = inputCount;
    }

    public long getOututCount() {
        return oututCount;
    }

    public void setOututCount(long oututCount) {
        this.oututCount = oututCount;
    }
}
