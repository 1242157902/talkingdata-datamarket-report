package com.talkingdata.dao;

import com.talkingdata.domain.*;
import com.talkingdata.utils.ClientUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import java.util.*;

/**
 *
 * 服务提供者提供的报表接口Dao层
 * User：    ysl
 * Date:   2016/7/15
 * Time:   17:57
 */
public class ServiceProviderDao {


    public static void main(String[] args)
    {
        try{
            ServiceProviderDao spd = new ServiceProviderDao();
            ServiceProvider serviceProvider =  spd.getServiceSouthInfor("logstash-gatewaylog", "gatewaylog", "dmp/another/devinfo/v1",0l,0l);
            System.out.println(" 服务："+serviceProvider.getServiceIdSouth());
            List<ServiceSouthInfor> serviceSouthInforList = serviceProvider.getServiceSouthInforList();
                for (ServiceSouthInfor serviceSouthInfor:serviceSouthInforList )
                {
                    String userId = serviceSouthInfor.getUserId();
                    System.out.println("      用户id:" + userId +
                            " 调用次数：" + serviceSouthInfor.getAccessNumber() +
                            "成功访问次数：" + serviceSouthInfor.getSuccessNumber());
                    Set<Servicelog> servicelogs = serviceSouthInfor.getServicelogs();
                    for (Servicelog servicelog:servicelogs)
                    {
                        System.out.println("   trackId"+servicelog.getTrackId()+"输入记录："+servicelog.getInRecords()+
                                                                            "  输出记录："+servicelog.getOutRecords());
                    }
                }
        }catch (Exception e)
        {
            System.out.println("程序异常！");
            e.printStackTrace();
        }

    }

    /**
     *通过服务提供者的服务id，[起始时间，结束时间]，来
     *  查询该服务的调用情况
     * @param indeces                   :索引库
     * @param type                      ：类型
     * @param serviceIdSouth            ：服务提供者提供的服务的id
     * @param startTimeMillions         :起始时间
     * @param endTimeMillions           ：结束时间
     * @return                          ：返回list的形式
     * @throws Exception
     */
    public ServiceProvider getServiceSouthInfor(String indeces,String type,String serviceIdSouth,
                                                long startTimeMillions,long endTimeMillions) throws Exception
    {
        MeasureDao measureDao = new MeasureDao();
        ServiceProvider serviceProvider = new ServiceProvider();

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(
                        QueryBuilders.termQuery("serviceidsouth.raw", serviceIdSouth)
                );
        if(startTimeMillions!=0l&&endTimeMillions!=0l&&endTimeMillions>=startTimeMillions)
        {
            boolQuery.must(
                    QueryBuilders.rangeQuery("startts")
                            .from(startTimeMillions)
                            .to(endTimeMillions)
            );
        }
        //获得客户端
        Client client = ClientUtils.getDefaultElasticSearchClient();
        SearchRequestBuilder sbuilder = client
                .prepareSearch(indeces)
                .setQuery(
                        boolQuery
                )
                .addAggregation(
                        AggregationBuilders
                                .terms("serviceidsouth")
                                .field("serviceidsouth.raw")        //服务标识索引
                                .subAggregation(
                                        AggregationBuilders
                                                .terms("userid")
                                                .field("userid.raw")        //服务索引
                                                .subAggregation(
                                                        AggregationBuilders
                                                                .terms("statusinner")       //调用返回状态
                                                                .field("statusinner")
                                                )
                                                .subAggregation(
                                                        AggregationBuilders
                                                                .terms("trackid")       //调用返回状态
                                                                .field("trackid.raw")
                                                )
                                )
                );
        //System.out.println("查询参数：--"+sbuilder);
        SearchResponse sr = sbuilder.execute().actionGet();
        //System.out.println("输出结果：--"+sr);
        Terms serviceidsouth = sr.getAggregations().get("serviceidsouth");
        // For each entry
        if(serviceidsouth!=null)
        {
            for (Terms.Bucket entry : serviceidsouth.getBuckets())
            {
                String serviceId = (String) entry.getKey();
                serviceProvider.setServiceIdSouth(serviceId);
                //System.out.println("serviceidsouth :--"+serviceId+" count:---"+ entry.getDocCount());
                Terms userid = entry.getAggregations().get("userid");
                if(userid!=null)
                {
                    List<ServiceSouthInfor> serviceSouthInforList = new ArrayList<>();
                    for (Terms.Bucket pairs : userid.getBuckets())
                    {
                        ServiceSouthInfor  serviceSouthInfor = new ServiceSouthInfor();
                        String userId = (String) pairs.getKey();
                        long userNumber = pairs.getDocCount();
                        serviceSouthInfor.setUserId(userId);
                        serviceSouthInfor.setAccessNumber(userNumber);
                        // System.out.println("       serviceidnorth :--" + userId + " count:---" + userNumber);
                        Terms statusinner = pairs.getAggregations().get("statusinner");
                        if (statusinner!=null)
                        {
                            for (Terms.Bucket pp:statusinner.getBuckets())
                            {
                                Long  name = (Long)pp.getKey();
                                long number = pp.getDocCount();
                                if(name==200)
                                {
                                    serviceSouthInfor.setSuccessNumber(number);
                                }
                               // System.out.println("            name:--"+name+"  count:--"+number);
                            }
                            serviceSouthInfor.setFailedNumber(userNumber-serviceSouthInfor.getSuccessNumber());
                        }
                        Terms trackids = pairs.getAggregations().get("trackid");
                        if(trackids!=null)
                        {
                            Set<Servicelog> servicelogs = new HashSet<Servicelog>();
                            for (Terms.Bucket trackid:trackids.getBuckets())
                            {
                                Servicelog servicelog = new Servicelog();
                                String  name =  (String)trackid.getKey();
                                servicelog.setTrackId(name);
                                long number = trackid.getDocCount();
                                //System.out.println("            tracid:--"+name+"  count:--"+number);
                                servicelogs.add(measureDao.statisticByTracekId("logstash-servicelog", "servicelog",servicelog));//trackid没有重复的情况下可以使用
                                 servicelogs.add(servicelog);
                            }
                            serviceSouthInfor.setServicelogs(servicelogs);
                        }
                        serviceSouthInforList.add(serviceSouthInfor);
                    }
                    serviceProvider.setServiceSouthInforList(serviceSouthInforList);
                }
            }
        }
        //关闭客户端
       // ClientUtils.closeClient(client);
        return serviceProvider;
    }
}
