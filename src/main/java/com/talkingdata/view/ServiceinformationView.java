package com.talkingdata.view;

import com.talkingdata.domain.ServiceInformation;

import java.util.List;

/**
 * 对应服务管理界面的包装类
 * User：    ysl
 * Date:   2016/8/16
 * Time:   11:01
 */
public class ServiceinformationView extends Status {

    private List<ServiceInformation>  data;

    public List<ServiceInformation> getData() {
        return data;
    }

    public void setData(List<ServiceInformation> data) {
        this.data = data;
    }
}
