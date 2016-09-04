package com.talkingdata.domain;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 对应servicelog的日志实体
 * User：    ysl
 * Date:   2016/7/6
 * Time:   17:18
 */
@XmlRootElement
public class Servicelog {

    private String trackId;                 //trackId
    private long inRecords;               //每一条servicelog对应的输入记录
    private long outRecords;                //每一条servicelog对应的输出记录


    public long getInRecords() {
        return inRecords;
    }

    public void setInRecords(long inRecords) {
        this.inRecords = inRecords;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public long getOutRecords() {
        return outRecords;
    }

    public void setOutRecords(long outRecords) {
        this.outRecords = outRecords;
    }
}
