package com.talkingdata.dao;

import com.talkingdata.domain.ServiceInformation;
import com.talkingdata.domain.WeekReport;
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
import java.util.List;

/**
 * 对应服务管理界面中的数据
 * User：    ysl
 * Date:   2016/8/16
 * Time:   10:42
 */
public class ServiceManagerDao {

    public static void main(String[] args)
    {
        ServiceManagerDao serviceManagerDao = new ServiceManagerDao();
        try {
            serviceManagerDao.getServiceInformations("logstash-gatewaylog", "gatewaylog",
                    "testservice", "testuser", 0l, 0l);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param indeces                           :索引库
     * @param type                              ：类型
     * @param serviceId                         ：服务id(可选)
     * @param userId                            ：用户id（可选）
     * @param startTimeMillions                 ：起始时间戳(可选)
     * @param endTimeMillions                   ：结束时间戳(可选)
     * @return
     * @throws Exception
     */
    public List<ServiceInformation> getServiceInformations(String indeces ,String type,
                                                     String serviceId,String userId,
                                                     long startTimeMillions,long endTimeMillions )throws Exception
    {
        List<ServiceInformation> serviceInformations = new ArrayList<ServiceInformation>();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if(!StringUtils.isEmpty(serviceId))
        {
            boolQuery .must(
                    QueryBuilders.termQuery("serviceid", serviceId)
            );
        }
        if(!StringUtils.isEmpty(userId))
        {
            boolQuery.filter(
                    QueryBuilders.termQuery("userid.raw",userId)
            );
        }
        if (startTimeMillions!=0l&&endTimeMillions!=0l)
        {
            boolQuery.filter(
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
                                .terms("serviceid")
                                .field("serviceid.raw")        //用户标识索引
                                .subAggregation(
                                        AggregationBuilders.terms("userid")
                                                            .field("userid.raw")
                                )
                                .subAggregation(
                                        AggregationBuilders
                                                .terms("statusinner")       //成功调用次数
                                                .field("statusinner")
                                )
                );
        //System.out.println("查询参数：--"+sbuilder);
        SearchResponse sr = sbuilder.execute().actionGet();
        //System.out.println("输出结果：--"+sr);
        Terms serviceids = sr.getAggregations().get("serviceid");
        // For each entry
        if(serviceids!=null)
        {
            for (Terms.Bucket entry : serviceids.getBuckets())
            {
                ServiceInformation serviceInformation = new ServiceInformation();
                String service_id = (String)entry.getKey();
                serviceInformation.setServiceid(service_id);
                long accessRequest = entry.getDocCount();
                serviceInformation.setAccessRequest(accessRequest);
               //System.out.println("serviceId :--" + service_id + " count:---" + entry.getDocCount());
                Terms statusinners = entry.getAggregations().get("statusinner");
                if(statusinners!=null)
                {
                    for (Terms.Bucket pairs : statusinners.getBuckets())
                    {
                        long statusinner = (long) pairs.getKey();
                        long successRequest = pairs.getDocCount();
                        //System.out.println("       statusinner :--" + statusinner + " count:---" + successRequest);
                        if(statusinner==200)
                        {
                            serviceInformation.setSuccessRequest(successRequest);
                        }
                    }
                }
                Terms userIds = entry.getAggregations().get("userid");
                if(userIds!=null)
                {
                   // System.out.println("    users:"+userIds.getBuckets().size());
                    serviceInformation.setAccessAccountNum(userIds.getBuckets().size());
                 /*   for (Terms.Bucket pairs : userIds.getBuckets())
                    {
                        String userName =  (String) pairs.getKey();
                        long accessAccountNum = pairs.getDocCount();
                        System.out.println("       userName :--" + userName + " count:---" + accessAccountNum);
                    }*/
                }
                serviceInformations.add(serviceInformation);
            }
        }
        return  serviceInformations;
    }
}
