package com.talkingdata.view;

import com.talkingdata.domain.DayReport;

import java.util.List;

/**
 * 图表界面的包装类
 * User：    ysl
 * Date:   2016/8/5
 * Time:   12:04
 */
public class GraphView extends  Status {

    List<DayReport> data;

    public List<DayReport> getData() {
        return data;
    }

    public void setData(List<DayReport> data) {
        this.data = data;
    }
}
