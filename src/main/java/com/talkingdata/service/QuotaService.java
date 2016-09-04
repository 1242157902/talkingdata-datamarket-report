package com.talkingdata.service;

import com.talkingdata.dao.QuotaDao;
import com.talkingdata.domain.*;
import com.talkingdata.utils.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * User：    ysl
 * Date:   2016/7/21
 * Time:   16:50
 */
public class QuotaService {
    private static final QuotaDao quotaDao = new QuotaDao();
    /**
     * 通过appkey、serviceIdNorth来获取现在调用的数量
     * @param appkey            :appkey
     * @param serviceIdNorth        ：服务id
     * @return
     * @throws Exception
     */
    public Appkey getUserQuotaUsed(String appkey,String serviceIdNorth) throws Exception
    {

        if(!StringUtils.isEmpty(appkey)&&
                    !StringUtils.isEmpty(serviceIdNorth))
        {
            Appkey ak = quotaDao.getQuotaUsed("logstash-gatewaylog","gatewaylog",appkey,serviceIdNorth);
            if(ak!=null)
            {
                List<ServiceInfor> serviceInforList =ak.getServiceInforList();
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
            return ak;
        }else{
            return null;
        }
    }

    /**
     *通过appkey和serviceIdNorth来查询用户的使用量
     * @param appkey                    :AppKey
     * @param serviceId            :暴漏给外部的服务
     * @return
     * @throws Exception
     */
    public Quota getQuotaUsed(String appkey,String serviceId) throws Exception
    {
        QuotaDao quotaDao = new QuotaDao();
        Quota quota = null;
        if(!StringUtils.isEmpty(appkey)&&
                !StringUtils.isEmpty(serviceId))
        {
            quota = new Quota();
            Appkey ak = quotaDao.getQuotaUsed("logstash-gatewaylog","gatewaylog",appkey,serviceId);
            if(ak!=null)
            {
                quota.setAppkey(appkey);
                quota.setServiceId(serviceId);
                List<ServiceInfor> serviceInforList =ak.getServiceInforList();
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
                    ServiceInfor serviceInfor = serviceInforList.get(0);
                    quota.setSuccessRequest(serviceInfor.getSuccessNumber());
                    quota.setOutRecords(serviceInfor.getOutRecordsSum());
                }
            }
            return quota;
        }else{
            return null;
        }
    }

    /**
     * 新的接口：quota接口
     * @param appkey        ：appkey
     * @param serviceId     ：服务id
     * @return
     * @throws Exception
     */
    public Quota getQuotaUsedNew(String appkey,String serviceId)throws  Exception
    {
        if(!StringUtils.isEmpty(appkey)&&
                !StringUtils.isEmpty(serviceId))
        {
            return quotaDao.getQuotaUsedNew("logstash-gatewaylog","gatewaylog",appkey,serviceId);
        }else
        {
            return null;
        }
    }

}
