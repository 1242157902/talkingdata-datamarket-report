package com.talkingdata.dao;

import com.talkingdata.domain.*;
import com.talkingdata.utils.ClientUtils;
import com.talkingdata.utils.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User：    ysl
 * Date:   2016/7/21
 * Time:   16:51
 */
public class QuotaDao {

    public static void main(String[] args)
    {
        QuotaDao quotaDao = new QuotaDao();
        try{
           // Appkey appkey = quotaDao.getQuotaUsed("logstash-gatewaylog","gatewaylog","5cf0cbfeecda4537b86e03bd4e00459a","dmp/devinfo/v4");
            Quota quota =  quotaDao.getQuotaUsedNew("logstash-gatewaylog","gatewaylog",
                    "57691b3d4095544a66842f59","8420ed30ae524d11859d69941e5092a8");
            System.out.println("  成功请求：" + quota.getSuccessRequest()
                    +" 输出记录："+quota.getOutRecords());
        }catch (Exception e)
        {
            System.out.println("程序异常！");
            e.printStackTrace();
        }

    }

    public Appkey getQuotaUsed(String indeces,String type,String appkeykey,String serviceId)throws  Exception
    {
        Appkey ak = new Appkey();
        MeasureDao measureDao = new MeasureDao();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(
                        QueryBuilders.termQuery("appkey", appkeykey)
                );
        if (!StringUtils.isEmpty(serviceId))
        {
            boolQuery.must(
                    QueryBuilders.termQuery("serviceid.raw", serviceId)
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
                                .terms("appkey")
                                .field("appkey.raw")        //服务标识索引
                                .subAggregation(
                                        AggregationBuilders
                                                .terms("serviceid")
                                                .field("serviceid.raw")        //服务索引
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
        sbuilder.setSize(0);
       // System.out.println("查询参数：--"+sbuilder);
        SearchResponse sr = sbuilder.execute().actionGet();
      // System.out.println("输出结果：--"+sr);
        Terms appkeys = sr.getAggregations().get("appkey");
        // For each entry
        if(appkeys!=null)
        {
            for (Terms.Bucket entry : appkeys.getBuckets())
            {
                String appkey = (String) entry.getKey();
                ak.setAppkey(appkey);
               // System.out.println("appkey :--" + appkey + " count:---" + entry.getDocCount());
                Terms serviceidnorth = entry.getAggregations().get("serviceid");
                if(serviceidnorth!=null)
                {
                    List<ServiceInfor> serviceInforList = new ArrayList<ServiceInfor>();
                    for (Terms.Bucket pairs : serviceidnorth.getBuckets())
                    {
                        ServiceInfor  serviceInfor = new ServiceInfor();
                        String serviceNothId = (String) pairs.getKey();
                        long useNumber = pairs.getDocCount();
                        serviceInfor.setServiceNorth(serviceNothId);
                        serviceInfor.setAccessNumber(useNumber);
                        //System.out.println("       serviceidnorth :--" + serviceNothId + " count:---" + useNumber);
                        Terms statusinner = pairs.getAggregations().get("statusinner");
                        if (statusinner!=null)
                        {
                            for (Terms.Bucket pp:statusinner.getBuckets())
                            {
                                Long  name = (Long)pp.getKey();
                                long number = pp.getDocCount();
                                if(name==200)
                                {
                                    serviceInfor.setSuccessNumber(number);
                                }
                                // System.out.println("            name:--"+name+"  count:--"+number);
                            }
                            serviceInfor.setFailedNumber(useNumber-serviceInfor.getSuccessNumber());
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
                                // System.out.println("            tracid:--" + name + "  count:--" + number);
                                servicelogs.add(measureDao.statisticByTracekId("logstash-servicelog", "servicelog", servicelog));//trackid没有重复的情况下可以使用
                            }
                            serviceInfor.setServicelogs(servicelogs);
                        }
                        serviceInforList.add(serviceInfor);
                    }
                    ak.setServiceInforList(serviceInforList);
                }
            }
        }
        //关闭客户端
        //ClientUtils.closeClient(client);
        return ak;
    }

    /**
     * 修改后的Quota接口，直接从gatewaylog中获取输入记录
     * @param indeces
     * @param type
     * @param appkeykey
     * @param serviceId
     * @return
     * @throws Exception
     */
    public Quota getQuotaUsedNew(String indeces,String type,String appkeykey,String serviceId)throws  Exception
    {
        Quota quota = new Quota();
        quota.setAppkey(appkeykey);
        quota.setServiceId(serviceId);
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(
                        QueryBuilders.termQuery("appkey", appkeykey)
                );
        if (!StringUtils.isEmpty(serviceId))
        {
            boolQuery.must(
                    QueryBuilders.termQuery("serviceid", serviceId)
            );
        }
        //获取客户端
        Client client = ClientUtils.getDefaultElasticSearchClient();
        SearchRequestBuilder sbuilder = client
                .prepareSearch(indeces)
                .setQuery(
                        boolQuery
                )
                .addAggregation(
                        AggregationBuilders
                                .terms("statusinner")       //调用返回状态
                                .field("statusinner")
                                .subAggregation(
                                        AggregationBuilders
                                                .sum("outputCount")
                                                .field("outputCount")
                                )
                );
        sbuilder.setSize(0);
       // System.out.println("查询参数：--"+sbuilder);
        SearchResponse sr = sbuilder.execute().actionGet();
        // System.out.println("输出结果：--"+sr);
        Terms statusinner = sr.getAggregations().get("statusinner");
        // For each entry
        if(statusinner!=null)
        {
        for (Terms.Bucket entry : statusinner.getBuckets())
        {
            long statusInnerStatus = (long) entry.getKey();
            long number = entry.getDocCount();
            if(statusInnerStatus==200)
            {
                quota.setSuccessRequest(number);
            }
          /*System.out.println("statusInnerStatus :--" + statusInnerStatus +
                    " count:---" + entry.getDocCount());*/
            Sum outputCount = entry.getAggregations().get("outputCount");
            double outputCountNumber = outputCount.getValue();
            quota.setOutRecords((long) outputCountNumber);
           /*System.out.println("outputCount :--" +
                    " count:---" + outputCountNumber);*/
        }
    }        //关闭客户端
        //ClientUtils.closeClient(client);
        return quota;
    }
}
