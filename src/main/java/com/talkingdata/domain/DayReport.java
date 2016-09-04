package com.talkingdata.domain;

/**
 * 一个用户一天的服务调用情况
 * User：    ysl
 * Date:   2016/8/5
 * Time:   14:20
 */
public class DayReport {

    private String strDate;                     //字符串日期
    private long  accessRequest;             //访问次数
    private long successRequest;            //成功调用次数
    private long inputCount;                //输入记录
    private long oututCount;                //输出记录


    public long getOututCount() {
        return oututCount;
    }

    public void setOututCount(long oututCount) {
        this.oututCount = oututCount;
    }

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
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

    public long getInputCount() {
        return inputCount;
    }

    public void setInputCount(long inputCount) {
        this.inputCount = inputCount;
    }
}
