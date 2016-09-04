package com.talkingdata.service;

import com.talkingdata.dao.ShrioDao;
import com.talkingdata.domain.Shrio;
import com.talkingdata.domain.WeekReport;
import com.talkingdata.utils.DateUtils;
import com.talkingdata.utils.PropertiesUtil;
import com.talkingdata.utils.StringUtils;

import java.util.List;

/**
 * 对应鉴权的日志的service层
 * User：    ysl
 * Date:   2016/8/12
 * Time:   12:02
 */
public class ShrioService {

    ShrioDao shrioDao = new ShrioDao();

    /**
     *查询对应的日志鉴权的访问准确率
     * @param startTime             :起始时间
     * @param endTime               ：结束时间
     * @return
     * @throws Exception
     */
    public Shrio getWeekServicesUsedInfor(String startTime,String endTime )throws Exception
    {
        if(!StringUtils.isEmpty(startTime))
        {
            long startTimeMillions = DateUtils.dateToMillions(DateUtils.strToDate(startTime));
            long endTimeMillions = 0l;
            if(StringUtils.isEmpty(endTime))
            {
                endTimeMillions = DateUtils.currentMillions();
            }else{
                endTimeMillions = DateUtils.dateToMillions(DateUtils.strToDate(endTime));
            }
            System.out.println(startTime + "  " + DateUtils.millionsToStrDate(endTimeMillions));
            return shrioDao.getSuccessAccessingRateOfDay("logstash-shirolog","shirolog",
                    startTimeMillions,endTimeMillions);
        }else{
            long endTimeMillions = DateUtils.currentMillions();
            String currentPath= this.getClass().getResource("/").getFile().toString();
            String strDay = PropertiesUtil.getProperties("shrioDay", currentPath + "/config.properties");
            int day = 1;
            if(!StringUtils.isEmpty(strDay))
            {
                day = Integer.parseInt(strDay);
            }
            long startTimeMillions = DateUtils.strToDate(DateUtils.millionsToStrDate(DateUtils.getBeforeDay(DateUtils.millionsToStrDate(endTimeMillions), day))).getTime();
            System.out.println(DateUtils.millionsToStrDate(startTimeMillions)+"  "+DateUtils.millionsToStrDate(endTimeMillions));
            return shrioDao.getSuccessAccessingRateOfDay("logstash-shirolog", "shirolog",
                    startTimeMillions, endTimeMillions);
        }
    }

}
