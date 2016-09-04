package com.talkingdata.service;

import com.talkingdata.dao.ServiceManagerDao;
import com.talkingdata.domain.ServiceInformation;
import com.talkingdata.utils.DateUtils;
import com.talkingdata.utils.StringUtils;

import java.util.List;

/**
 * 对应服务管理界面
 * User：    ysl
 * Date:   2016/8/16
 * Time:   11:52
 */
public class ServiceManagerService {

    private static final ServiceManagerDao serviceManagerDao = new ServiceManagerDao();

    /**
     *对应服务管理界面，所有服务的列表，或者通过相应的服务id，查询服务的被调用情况
     *          包含：表用账户数，调用次数，成功次数
     * @param serviceId             :服务id
     * @param userId                ：用户id
     * @param startTime             ：起始时间
     * @param endTime               ：结束时间
     * @return
     * @throws Exception
     */
    public List<ServiceInformation> getServiceInformations(String serviceId,String userId,
                                                           String startTime,String endTime )throws Exception
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
            return  serviceManagerDao.getServiceInformations("logstash-gatewaylog","gatewaylog",
                    serviceId,userId,startTimeMillions,endTimeMillions);
        }else{
            return  serviceManagerDao.getServiceInformations("logstash-gatewaylog","gatewaylog",
                    serviceId,userId,0l,0l);
        }
    }

}
