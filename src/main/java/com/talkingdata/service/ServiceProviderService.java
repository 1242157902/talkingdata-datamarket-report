package com.talkingdata.service;

import com.talkingdata.dao.ServiceProviderDao;
import com.talkingdata.domain.ServiceInfor;
import com.talkingdata.domain.ServiceProvider;
import com.talkingdata.domain.ServiceSouthInfor;
import com.talkingdata.domain.Servicelog;
import com.talkingdata.utils.DateUtils;
import com.talkingdata.utils.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * 针对服务提供者的提供服务的报表信息
 * User：    ysl
 * Date:   2016/7/19
 * Time:   13:50
 */
public class ServiceProviderService {

    ServiceProviderDao serviceProviderDao = new ServiceProviderDao();

    /**
     *服务提供商的服务的调用情况
     * @param serviceIdSouth            :服务id
     * @param startTime                 ：起始时间
     * @param endTime                   ：结束时间
     * @return
     * @throws Exception
     */
    public ServiceProvider getServiceBySouthInfor(String serviceIdSouth,String startTime,String endTime) throws Exception
    {
        ServiceProvider serviceProvider = null;
        if(!StringUtils.isEmpty(serviceIdSouth)&&!StringUtils.isEmpty(startTime))
        {
            long startTimeMillions = DateUtils.dateToMillions(DateUtils.strToDate(startTime));
            long endTimeMillions = 0l;
            if(StringUtils.isEmpty(endTime))
            {
                endTimeMillions = DateUtils.currentMillions();
            }else{
                endTimeMillions = DateUtils.dateToMillions(DateUtils.strToDate(endTime));
            }
            serviceProvider =serviceProviderDao.getServiceSouthInfor("logstash-gatewaylog","gatewaylog",
                    serviceIdSouth,startTimeMillions,endTimeMillions);

        }else if(!StringUtils.isEmpty(serviceIdSouth)&&StringUtils.isEmpty(startTime)){
            serviceProvider = serviceProviderDao.getServiceSouthInfor("logstash-gatewaylog","gatewaylog",serviceIdSouth,0l,0l);
        }else {
            serviceProvider =  null;
        }

        if(serviceProvider!=null)
        {
            List<ServiceSouthInfor> serviceSouthInfors = serviceProvider.getServiceSouthInforList();
            if(serviceSouthInfors.size()>0)
            {
                for (ServiceSouthInfor serviceSouthInfor:serviceSouthInfors)
                {
                    Set<Servicelog> servicelogs = serviceSouthInfor.getServicelogs();
                    if(servicelogs.size()>0)
                    {
                        long inRecordsSum = 0l;
                        long outRecordsSum=0l;
                        for (Servicelog servicelog:servicelogs)
                        {
                            inRecordsSum +=servicelog.getInRecords();
                            outRecordsSum +=servicelog.getOutRecords();
                        }
                        serviceSouthInfor.setInRecordsSum(inRecordsSum);
                        serviceSouthInfor.setOutRecordsSum(outRecordsSum);
                    }
                }
            }
        }
        return serviceProvider;
    }

}
