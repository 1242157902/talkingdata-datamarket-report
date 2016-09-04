package com.talkingdata.domain;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * 针对服务提供者提供的报表信息
 * User：    ysl
 * Date:   2016/7/19
 * Time:   11:14
 */
@XmlRootElement
public class ServiceProvider {

    private String serviceIdSouth;              //服务提供者的id
    private String serviceSouthName;            //服务提供者的服务的名称

    /**
     * 通过服务的serviceidsouth:
     *          了解，哪些appkey
     *                      每个appkey的调用情况：调用次数，失败次数，输入记录，输出记录
     *
     */
    List<ServiceSouthInfor> serviceSouthInforList;

    public String getServiceIdSouth() {
        return serviceIdSouth;
    }

    public void setServiceIdSouth(String serviceIdSouth) {
        this.serviceIdSouth = serviceIdSouth;
    }

    public String getServiceSouthName() {
        return serviceSouthName;
    }

    public void setServiceSouthName(String serviceSouthName) {
        this.serviceSouthName = serviceSouthName;
    }

    public List<ServiceSouthInfor> getServiceSouthInforList() {
        return serviceSouthInforList;
    }

    public void setServiceSouthInforList(List<ServiceSouthInfor> serviceSouthInforList) {
        this.serviceSouthInforList = serviceSouthInforList;
    }
}
