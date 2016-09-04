package com.talkingdata.view;

import com.talkingdata.domain.WeekReport;

import java.util.List;

/**
 * 包装报表报表实体
 * User：    ysl
 * Date:   2016/8/5
 * Time:   10:41
 */
public class MyAccountView extends  Status{

    List<WeekReport>  datas;

    public List<WeekReport> getDatas() {
        return datas;
    }

    public void setDatas(List<WeekReport> datas) {
        this.datas = datas;
    }
}
