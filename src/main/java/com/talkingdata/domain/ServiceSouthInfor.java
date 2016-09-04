package com.talkingdata.domain;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

/**
 *服务提供者提供的服务的调用信息
 * User：    ysl
 * Date:   2016/7/19
 * Time:   11:03
 */
@XmlRootElement
public class ServiceSouthInfor {

    private String userId;              //调用者的id
    private String userName;            //调用者名称


    private long accessNumber ;                 //调用次数
    private long successNumber ;                //成功次数
    private long failedNumber   ;               //失败次数
    private long inRecordsSum;                   //输入记录总数
    private long outRecordsSum;                  //输出记录总数


    Set<Servicelog> servicelogs;                 //每种服务下调用的日志记录情况


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
}
