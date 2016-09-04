package com.talkingdata.domain;

/**
 * 对应服务管理界面上的数据实体
 * User：    ysl
 * Date:   2016/8/16
 * Time:   10:54
 */
public class ServiceInformation {

    private String serviceid;                   //具体的服务id
    private long accessAccountNum;            //调用账户数
    private long  accessRequest;             //访问次数
    private long successRequest;            //成功调用次数

    public String getServiceid() {
        return serviceid;
    }

    public void setServiceid(String serviceid) {
        this.serviceid = serviceid;
    }

    public long getAccessAccountNum() {
        return accessAccountNum;
    }

    public void setAccessAccountNum(long accessAccountNum) {
        this.accessAccountNum = accessAccountNum;
    }

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
}
