package com.talkingdata.service;

import com.talkingdata.dao.MyAccountDao;
import com.talkingdata.domain.*;
import com.talkingdata.utils.DateUtils;
import com.talkingdata.utils.StringUtils;
import org.elasticsearch.common.collect.CopyOnWriteHashSet;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 用户登录后的服务报表界面
 *      所使用的服务层
 * User：    ysl
 * Date:   2016/7/14
 * Time:   10:54
 */
public class MyAccountService {

    private   static final MyAccountDao myAccountDao = new MyAccountDao();

    public static void main(String[] args)
    {
        MyAccountService myAccountService = new MyAccountService();
        try {
            List<DayReport> dayReports = myAccountService.getDataOfSomeTime("testappuserid","testservice",7);
            for(DayReport dayReport:dayReports)
            {
                System.out.println(dayReport.getStrDate()+"  "+dayReport.getAccessRequest());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *通过下面的参数，查询该用户拥各个app的调用情况
     * @param userName      :用户名称
     * @param serviceIdNorth :服务id
     * @param startTime     ：起始时间
     * @param endTime       ：结束时间
     * @return
     */
    public User getUserServices(String userName,String serviceIdNorth,String startTime,String endTime)throws  Exception
    {
        System.out.println(serviceIdNorth);
        if(!StringUtils.isEmpty(userName)&&
                !StringUtils.isEmpty(serviceIdNorth)&&
                !StringUtils.isEmpty(startTime))
        {
            long startTimeMillions = DateUtils.dateToMillions(DateUtils.strToDate(startTime));
            long endTimeMillions = 0l;
            if(StringUtils.isEmpty(endTime))
            {
                endTimeMillions = DateUtils.currentMillions();
            }else{
                endTimeMillions = DateUtils.dateToMillions(DateUtils.strToDate(endTime));
            }
            return resetNumbers(myAccountDao.getUserServices("logstash-gatewaylog","gatewaylog",userName,
                    serviceIdNorth,startTimeMillions,endTimeMillions));
        }else if(!StringUtils.isEmpty(userName)&&
                StringUtils.isEmpty(serviceIdNorth)&&
                !StringUtils.isEmpty(startTime))
        {
            long startTimeMillions = DateUtils.dateToMillions(DateUtils.strToDate(startTime));
            long endTimeMillions = 0l;
            if(StringUtils.isEmpty(endTime))
            {
                endTimeMillions = DateUtils.currentMillions();
            }else{
                endTimeMillions = DateUtils.dateToMillions(DateUtils.strToDate(endTime));
            }
            return resetNumbers(myAccountDao.getUserServices("logstash-gatewaylog","gatewaylog",userName,
                                                null,startTimeMillions,endTimeMillions));
        }else if(!StringUtils.isEmpty(userName)&&
                StringUtils.isEmpty(serviceIdNorth)&&
                    StringUtils.isEmpty(startTime)){
            return resetNumbers(myAccountDao.getUserServices("logstash-gatewaylog", "gatewaylog", userName,
                    null,0l,0l));
        }else if(!StringUtils.isEmpty(userName)&&
                !StringUtils.isEmpty(serviceIdNorth)&&
                StringUtils.isEmpty(startTime))
        {
            return resetNumbers(myAccountDao.getUserServices("logstash-gatewaylog", "gatewaylog", userName,
                    serviceIdNorth,0l,0l));
        }else
        {
            return null;
        }
    }
    /**
     *通过方法加和里面的输入记录和输出记录
     * @param user      :用户实体
     * @return
     */
    public User resetNumbers(User user)
    {
        if(user!=null)
        {
            List<Appkey> appkeys = user.getAppkeys();
            if(appkeys!=null&&appkeys.size()>0)
            {
                for (Appkey appkey: appkeys)
                {
                    List<ServiceInfor> serviceInforList = appkey.getServiceInforList();
                    if(serviceInforList!=null&&serviceInforList.size()>0)
                    {
                        for (ServiceInfor serviceInfor:serviceInforList)
                        {
                            Set<Servicelog> servicelogs = serviceInfor.getServicelogs();
                            if(servicelogs!=null&&servicelogs.size()>0)
                            {
                                long inRecordsSum = 0l;
                                long outRecordsSum=0l;
                                for(Servicelog servicelog:servicelogs)
                                {
                                    inRecordsSum +=servicelog.getInRecords();
                                    outRecordsSum +=servicelog.getOutRecords();
                                    // measureDao.statisticByTracekId("logstash-servicelog", "servicelog", servicelog);
                                    // System.out.println("              该服务调用的trackid:" + servicelog.getTrackId());
                                }
                                serviceInfor.setInRecordsSum(inRecordsSum);
                                serviceInfor.setOutRecordsSum(outRecordsSum);
                                //System.out.println("           输入记录数："+inRecordsSum+" 输出记录数："+outRecordsSum);
                            }
                        }
                    }
                }
                return  user;
            }else
            {
                return  user;
            }
        }else
        {
            return  null;
        }
    }


    /**
     *通过用户id、服务id,起始时间、结束时间来查询服务的情况
     * @param userId            :用户id
     * @param serviceId         ：服务id          可选
     * @param startTime         ：起始时间       可选
     * @param endTime           ：结束时间       可选
     * @return
     * @throws Exception
     */
    public List<WeekReport> getServicesByParams(  String userId,
                                                  String serviceId,
                                                  String startTime,
                                                  String endTime)throws Exception
    {
        if(!StringUtils.isEmpty(userId)&&!StringUtils.isEmpty(serviceId)
                &&!StringUtils.isEmpty(startTime))
        {
            long startTimeMillions = DateUtils.dateToMillions(DateUtils.strToDate(startTime));
            long endTimeMillions = 0l;
            if(StringUtils.isEmpty(endTime))
            {
                endTimeMillions = DateUtils.currentMillions();
            }else{
                endTimeMillions = DateUtils.dateToMillions(DateUtils.strToDate(endTime));
            }
            return myAccountDao.getWeekServicesUsedInfor("logstash-gatewaylog","gatewaylog",
                    userId,serviceId,startTimeMillions, endTimeMillions);
        }else if(!StringUtils.isEmpty(userId)
                &&StringUtils.isEmpty(startTime)){
            return myAccountDao.getWeekServicesUsedInfor("logstash-gatewaylog","gatewaylog",
                    userId,serviceId,0l, 0l);
        }else if(!StringUtils.isEmpty(userId)&&!StringUtils.isEmpty(startTime)){
            long startTimeMillions = DateUtils.dateToMillions(DateUtils.strToDate(startTime));
            long endTimeMillions = 0l;
            if(StringUtils.isEmpty(endTime))
            {
                endTimeMillions = DateUtils.currentMillions();
            }else{
                endTimeMillions = DateUtils.dateToMillions(DateUtils.strToDate(endTime));
            }
            return myAccountDao.getWeekServicesUsedInfor("logstash-gatewaylog","gatewaylog",
                    userId,serviceId,startTimeMillions, endTimeMillions);
        }else{
            return  null;
        }
    }

    /**
     * 用于前台界面中，图表形式的数据展示
     * @param userId                :用户id
     * @param serviceId             ：服务id
     * @param beforeDays            ：最近几天的数
     * @return
     * @throws Exception
     */
    public List<DayReport> getDataOfSomeTime(String userId,String serviceId,
                                  int beforeDays)throws  Exception
    {
        List<DayReport> dayReports = null;
        if(!StringUtils.isEmpty(userId)&&!StringUtils.isEmpty(serviceId))
        {
            dayReports = new ArrayList<DayReport>();
            //统计得到当天的调用统计
            long startTime = DateUtils.dateToMillions(DateUtils.strToDate(DateUtils.dateToShortStr(new Date()) + " 00:00:00"));
            long endTime = DateUtils.currentMillions();
            DayReport report = myAccountDao.getDayServicesUsedInfor("logstash-gatewaylog", "gatewaylog",
                    userId, serviceId, startTime, endTime);
            //report.setStrDate(DateUtils.millionsToStrZhDate(startTime));
            report.setStrDate((startTime/1000)+"");
            dayReports.add(report);

            long endTimeMillions = DateUtils.strToDate(DateUtils.dateToShortStr(new Date())+" 00:00:00").getTime();
            for(int i=1;i<beforeDays;i++)
            {
                long startTimeMillions = DateUtils.getBeforeDay(DateUtils.millionsToStrDate(endTimeMillions), 1);
                DayReport dayReport = myAccountDao.getDayServicesUsedInfor("logstash-gatewaylog", "gatewaylog",
                        userId, serviceId, startTimeMillions, endTimeMillions);
                //dayReport.setStrDate(DateUtils.millionsToStrZhDate(startTimeMillions));
                dayReport.setStrDate((startTimeMillions/1000)+"");
                dayReports.add(dayReport);
                /*System.out.println("  startTimeMillions" + DateUtils.millionsToStrDate(startTimeMillions)+
                        " endTimeMillions:"+DateUtils.millionsToStrDate(endTimeMillions));*/
                endTimeMillions = startTimeMillions ;
            }
        }
       return dayReports;
    }


}
