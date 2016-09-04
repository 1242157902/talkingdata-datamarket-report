package com.talkingdata.service;

import com.talkingdata.dao.MeasureDao;
import com.talkingdata.domain.MyServiceInfor;
import com.talkingdata.domain.ServiceInfor;
import com.talkingdata.domain.Servicelog;
import com.talkingdata.utils.DateUtils;
import com.talkingdata.utils.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * User：    ysl
 * Date:   2016/7/6
 * Time:   16:01
 */
public class MeasureService {

    MeasureDao measureDao = new MeasureDao();


    public List<MyServiceInfor> getUsersServices() throws Exception
    {
             long a = System.currentTimeMillis();
            List<MyServiceInfor> myServiceInfors = measureDao.getUsersServices("logstash-gatewaylog");

            for(MyServiceInfor myServiceInfor:myServiceInfors)
            {
               //System.out.println(" 用户名称："+myServiceInfor.getUserName());
                List<ServiceInfor> serviceInforList = myServiceInfor.getServiceInforList();
                for(ServiceInfor serviceInfor:serviceInforList)
                {
                    /*System.out.println("         服务名称："+serviceInfor.getServiceNorth()+
                            "  调用次数："+serviceInfor.getAccessNumber()+
                            " 成功次数："+serviceInfor.getSuccessNumber()+
                            " 失败次数："+serviceInfor.getFailedNumber());*/
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
        long b = System.currentTimeMillis();
        System.out.println("耗费时间为："+(b-a) );
            return myServiceInfors;
    }

    /**
     * 查询该段时间内的所有用户的服务
     * @param startTime      :起始时间调用情况
     * @param endTime       ：结束时间
     * @return
     */
    public List<MyServiceInfor> getTimeServices(String startTime,String endTime)throws Exception
    {
        if (!StringUtils.isEmpty(startTime))
        {
            long startTimeMillions = DateUtils.dateToMillions(DateUtils.strToDate(startTime));
            long endTimeMillions = 0l;
            if(StringUtils.isEmpty(endTime))
            {
                endTimeMillions = DateUtils.currentMillions();
            }else{
                endTimeMillions = DateUtils.dateToMillions(DateUtils.strToDate(endTime));
            }
           return  resetNumbers(measureDao.getTimeServices("logstash-gatewaylog", "gatewaylog", startTimeMillions, endTimeMillions));
        }else{
            return null;
        }
    }

    /**
     * 查询某一用户，某段时间内服务的调用情况
     * @param userId                        ：用户id
     * @param startTime             ：起始时间
     * @param endTime               ：结束时间
     * @return
     * @throws Exception
     */
    public List<MyServiceInfor> statisticByUserIdAndTime(String userId,String startTime,String endTime) throws Exception
    {
        if(!StringUtils.isEmpty(startTime)&&!StringUtils.isEmpty(userId))
        {
            long startTimeMillions = DateUtils.dateToMillions(DateUtils.strToDate(startTime));
            long endTimeMillions = 0l;
            if(StringUtils.isEmpty(endTime))
            {
                endTimeMillions = DateUtils.currentMillions();
            }else{
                endTimeMillions = DateUtils.dateToMillions(DateUtils.strToDate(endTime));
            }
            return  resetNumbers(measureDao.statisticByUserIdAndTime("logstash-gatewaylog", "gatewaylog", userId, startTimeMillions, endTimeMillions));
        }else{
            return  null;
        }
    }

    /**
     * 查询某一用户，某一服务，某段时间内的服务的调用情况
     * @param userId            ：用户id
     * @param serviceId         ：服务id
     * @param startTime ：起始时间
     * @param endTime   ：结束时间
     * @return                      ：服务的调用情况
     * @throws Exception
     */
    public List<MyServiceInfor> statisticByUserIdAndTimeAndServiceId(String userId,String serviceId,String startTime,String endTime) throws Exception
    {
        if(!StringUtils.isEmpty(userId)&&
                !StringUtils.isEmpty(serviceId)&&
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
            return  resetNumbers(measureDao.statisticByUserIdAndTimeAndServiceId("logstash-gatewaylog",
                    "gatewaylog", userId, serviceId, startTimeMillions, endTimeMillions));
        }else{
            return  null;
        }
    }

    /**
     * 查询各个服务的调用情况
     * @param userId            ：用户id
     * @return                  ：List<MyServiceInfor>
     * @throws Exception
     */
    public List<MyServiceInfor> statisticByUserId(String userId) throws Exception
    {
        if(!StringUtils.isEmpty(userId))
        {
            return  resetNumbers(measureDao.statisticByUserId("logstash-gatewaylog", "gatewaylog", userId));
        }else{
            return null;
        }
    }

    /**
     *  通过用户id,服务id来搜索调用情况
     * @param userId            :用户id
     * @param serviceId         ：服务id
     * @return
     * @throws Exception
     */
    public List<MyServiceInfor> statisticByUserIdAndServiceId(String userId,String serviceId) throws Exception
    {
        if(!StringUtils.isEmpty(userId)&&!StringUtils.isEmpty(serviceId))
        {
           return  resetNumbers(measureDao.statisticByUserIdAndServiceId("logstash-gatewaylog", "gatewaylog", userId, serviceId));
        }else{
            return null;
        }
    }

    /**
     * 将里面的数据重新加和
     * @param myServiceInfors       :list服务
     * @return
     */
    public  List<MyServiceInfor> resetNumbers( List<MyServiceInfor> myServiceInfors)
    {
        if (myServiceInfors!=null&&myServiceInfors.size()>0)
        {
            for(MyServiceInfor myServiceInfor:myServiceInfors)
            {
                //System.out.println(" 用户名称："+myServiceInfor.getUserName());
                List<ServiceInfor> serviceInforList = myServiceInfor.getServiceInforList();
                for(ServiceInfor serviceInfor:serviceInforList)
                {
                    /*System.out.println("         服务名称："+serviceInfor.getServiceNorth()+
                            "  调用次数："+serviceInfor.getAccessNumber()+
                            " 成功次数："+serviceInfor.getSuccessNumber()+
                            " 失败次数："+serviceInfor.getFailedNumber());*/
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
            return myServiceInfors;
        }else
        {
            return null;
        }

    }
}
