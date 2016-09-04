package com.talkingdata.view;

import com.talkingdata.domain.Shrio;

/**
 * 包装Shrio ，返回鉴权的访问次数，成功次数
 * User：    ysl
 * Date:   2016/8/12
 * Time:   13:27
 */
public class ShrioView extends Status {

    private Shrio data;

    public Shrio getData() {
        return data;
    }

    public void setData(Shrio data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ShrioView{" +
                "data=" + data.toString() +
                '}';
    }
}
