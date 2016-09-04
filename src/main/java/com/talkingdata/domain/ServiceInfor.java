package com.talkingdata.domain;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Set;

/**
 * 暴漏给外部服务的内部信息
 * User：    ysl
 * Date:   2016/6/30
 * Time:   17:10
 */
@XmlRootElement
public class ServiceInfor {

    private String serviceNorth;                 //暴漏给外部的服务id
    private String serviceName ;                //服务名称

    private long accessNumber ;                 //调用次数
    private long successNumber ;                //成功次数
    private long failedNumber   ;               //失败次数
    private long inRecordsSum;                   //输入记录总数
    private long outRecordsSum;                  //输出记录总数


    Set<Servicelog> servicelogs;                 //每种服务下调用的日志记录情况


    public long getInRecordsSum() {
        return inRecordsSum;
    }

    public void setInRecordsSum(long inRecordsSum) {
        this.inRecordsSum = inRecordsSum;
    }

    public long getOutRecordsSum() {
        return outRecordsSum;
    }

    public void setOutRecordsSum(long outRecordsSum) {
        this.outRecordsSum = outRecordsSum;
    }

    public Set<Servicelog> getServicelogs() {
        return servicelogs;
    }

    public void setServicelogs(Set<Servicelog> servicelogs) {
        this.servicelogs = servicelogs;
    }

    public String getServiceNorth() {
        return serviceNorth;
    }

    public void setServiceNorth(String serviceNorth) {
        this.serviceNorth = serviceNorth;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public long getAccessNumber() {
        return accessNumber;
    }

    public void setAccessNumber(long accessNumber) {
        this.accessNumber = accessNumber;
    }

    public long getSuccessNumber() {
        return successNumber;
    }

    public void setSuccessNumber(long successNumber) {
        this.successNumber = successNumber;
    }

    public long getFailedNumber() {
        return failedNumber;
    }

    public void setFailedNumber(long failedNumber) {
        this.failedNumber = failedNumber;
    }
}
