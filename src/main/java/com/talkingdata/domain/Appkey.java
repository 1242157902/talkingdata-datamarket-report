package com.talkingdata.domain;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 *
 * Appkey实体,可以知道该appkey调用的服务有哪些？以及总的
 *      调用次数、成功次数、输入记录、输出记录数
 * User：    ysl
 * Date:   2016/7/21
 * Time:   10:58
 */
@XmlRootElement
public class Appkey {
    private String appkey ;             //用户appkey
    private List<ServiceInfor> serviceInforList;	//每个appkey可以调用多个服务

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public List<ServiceInfor> getServiceInforList() {
        return serviceInforList;
    }

    public void setServiceInforList(List<ServiceInfor> serviceInforList) {
        this.serviceInforList = serviceInforList;
    }
}
