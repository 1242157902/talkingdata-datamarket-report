package com.talkingdata.service;

import com.talkingdata.dao.WeekReportDao;
import com.talkingdata.domain.WeekReport;
import com.talkingdata.utils.DateUtils;
import com.talkingdata.utils.PropertiesUtil;
import com.talkingdata.utils.StringUtils;

import java.util.List;

/**
 * 服务报表统计的的service层
 * User：    ysl
 * Date:   2016/7/29
 * Time:   10:54
 */
public class WeekReportService {

     WeekReportDao  weekReportDao = new WeekReportDao();
    /**
     * 统计每种服务的 调用情况：
     *              返回服务id，成功调用次数、输入记录、输出记录
     * @param startTime         :起始时间
     * @param endTime           ：结束时间
     * @return                             返回数据
     * @throws Exception
     */
    public List<WeekReport> getWeekServicesUsedInfor(String startTime,String endTime )throws Exception
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
            System.out.println(startTime+"  "+DateUtils.millionsToStrDate(endTimeMillions));
            return weekReportDao.getWeekServicesUsedInfor("logstash-gatewaylog","gatewaylog",
                                                                startTimeMillions,endTimeMillions);
        }else{
            long endTimeMillions = DateUtils.currentMillions();
            String currentPath= this.getClass().getResource("/").getFile().toString();
            String strDay = PropertiesUtil.getProperties("day", currentPath + "/config.properties");
            int day = 1;
            if(!StringUtils.isEmpty(strDay))
            {
                day = Integer.parseInt(strDay);
            }
            long startTimeMillions = DateUtils.strToDate(DateUtils.millionsToStrDate(DateUtils.getBeforeDay(DateUtils.millionsToStrDate(endTimeMillions), day))).getTime();
            System.out.println(DateUtils.millionsToStrDate(startTimeMillions)+"  "+DateUtils.millionsToStrDate(endTimeMillions));
            return weekReportDao.getWeekServicesUsedInfor("logstash-gatewaylog","gatewaylog",
                    startTimeMillions,endTimeMillions);
        }
    }
}
