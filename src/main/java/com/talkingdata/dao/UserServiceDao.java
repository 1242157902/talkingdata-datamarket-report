package com.talkingdata.dao;

import com.talkingdata.domain.ServiceInfor;
import com.talkingdata.utils.ClientUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import java.util.Date;
import java.util.List;

/**
 * User：    ysl
 * Date:   2016/6/30
 * Time:   17:07
 */
public class UserServiceDao {

    public static void main(String[] args)
    {
        /*UserServiceDao usd = new UserServiceDao();
        try{
            usd.getUsersServiceByUserId("user","logstash-gatewaylog-2016.06.29");
        }catch (Exception e)
        {
            System.out.println("程序发生异常！");
            e.printStackTrace();
        }*/

        /*UserServiceDao usd = new UserServiceDao();
        try{
            usd.searchUsersServices("logstash-gatewaylog-2016.06.29", "userid", "marketUser1");
        }catch (Exception e)
        {
            System.out.println("程序发生异常！");
            e.printStackTrace();
        }*/

      /*  UserServiceDao usd = new UserServiceDao();
        try{
            usd.getUsersServices("logstash-gatewaylog-2016.06.29");
        }catch (Exception e)
        {
            System.out.println("程序发生异常！");
            e.printStackTrace();
        }*/

        UserServiceDao usd = new UserServiceDao();
        try{
            usd.getUsersServices("logstash-gatewaylog",1466583333760l,1466583333769l);
        }catch (Exception e)
        {
            System.out.println("程序发生异常！");
            e.printStackTrace();
        }

    }

    /**
     * 通过用户id（userId)来获取该用户调用的那些服务
     * @param userId
     * @return
     */
    public List<ServiceInfor> getUsersServiceByUserId(String userId,String indeces) throws Exception
    {
        Client client = ClientUtils.getDefaultElasticSearchClient();
        /**
         * 获得不同用户，不同服务，不同时间段 的调用次数，
         */
        SearchRequestBuilder sbuilder = client
                .prepareSearch(indeces)
                .setQuery(
                        QueryBuilders.rangeQuery("startts")//
                                .from(Long.parseLong("1466583333760"))
                                .to(Long.parseLong("1466583333769"))
                )
                .addAggregation(
                        AggregationBuilders
                                .terms("userid")
                                .field("userid.raw")
                                .subAggregation(
                                        AggregationBuilders
                                                .terms("serviceidnorth")
                                                .field("serviceidnorth.raw")
                                                .subAggregation(
                                                        AggregationBuilders
                                                                .terms("statusinner")
                                                                .field("statusinner")
                                                )
                                )
                );
        System.out.println("查询参数：--"+sbuilder);
        SearchResponse sr = sbuilder.execute().actionGet();
        //System.out.println("输出结果：--"+sr);
        Terms userid = sr.getAggregations().get("userid");
        // For each entry
        for (Terms.Bucket entry : userid.getBuckets())
        {
            String key = (String) entry.getKey();
            System.out.println("serviceidnorth :--"+key+" count:---"+ entry.getDocCount());
            Terms appkey = entry.getAggregations().get("serviceidnorth");
            for (Terms.Bucket pairs : appkey.getBuckets())
            {
                String appKey = (String) pairs.getKey();
                System.out.println("       serviceidnorth :--"+appKey+" count:---"+ pairs.getDocCount());
                Terms statusinner = pairs.getAggregations().get("statusinner");
                if (statusinner!=null)
                {
                    for (Terms.Bucket pp:statusinner.getBuckets())
                    {
                        Long  name = (Long)pp.getKey();
                        System.out.println("            name:--"+name+"  count:--"+pp.getDocCount());
                    }
                }

            }
        }
        //ClientUtils.closeClient(client);
        return null;
    }

    /**
     * 需要获得不同用户、不同服务的调用次数
     * @param indeces
     * @return
     * @throws Exception
     */
    public List<ServiceInfor> getUsersServices(String indeces) throws Exception
    {
        Client client = ClientUtils.getDefaultElasticSearchClient();
        /**
         * 获得不同用户，不同服务，不同时间段 的调用次数，
         */
        SearchRequestBuilder sbuilder = client
                .prepareSearch(indeces)
                .setQuery(
                        QueryBuilders.matchAllQuery()
                )
                .addAggregation(
                        AggregationBuilders
                                .terms("userid")
                                .field("userid.raw")
                                .subAggregation(
                                        AggregationBuilders
                                                .terms("serviceidnorth")
                                                .field("serviceidnorth.raw")
                                                .subAggregation(
                                                        AggregationBuilders
                                                                .terms("statusinner")
                                                                .field("statusinner")
                                                )
                                )
                );
        System.out.println("查询参数：--"+sbuilder);
        SearchResponse sr = sbuilder.execute().actionGet();
        //System.out.println("输出结果：--"+sr);
        Terms userid = sr.getAggregations().get("userid");
        // For each entry
        for (Terms.Bucket entry : userid.getBuckets())
        {
            String key = (String) entry.getKey();
            System.out.println("serviceidnorth :--"+key+" count:---"+ entry.getDocCount());
            Terms appkey = entry.getAggregations().get("serviceidnorth");
            for (Terms.Bucket pairs : appkey.getBuckets())
            {
                String appKey = (String) pairs.getKey();
                System.out.println("       serviceidnorth :--"+appKey+" count:---"+ pairs.getDocCount());
                Terms statusinner = pairs.getAggregations().get("statusinner");
                if (statusinner!=null)
                {
                    for (Terms.Bucket pp:statusinner.getBuckets())
                    {
                        Long  name = (Long)pp.getKey();
                        System.out.println("            name:--"+name+"  count:--"+pp.getDocCount());
                    }
                }

            }
        }
       // ClientUtils.closeClient(client);
        return null;
    }

    /**
     * 需要获得不同用户、不同服务的调用次数
     * @param indeces
     * @return
     * @throws Exception
     */
    public List<ServiceInfor> getUsersServices(String indeces,long startTime,long endTime) throws Exception
    {
        Client client = ClientUtils.getDefaultElasticSearchClient();
        /**
         * 获得不同用户，不同服务，不同时间段 的调用次数，
         */
        SearchRequestBuilder sbuilder = client
                .prepareSearch(indeces)
                .setQuery(
                        QueryBuilders.rangeQuery("startts")                     //时间字段
                                .from(startTime)
                                .to(endTime)
                )
                .addAggregation(
                        AggregationBuilders
                                .terms("userid")
                                .field("userid.raw")
                                .subAggregation(
                                        AggregationBuilders
                                                .terms("serviceidnorth")
                                                .field("serviceidnorth.raw")
                                                .subAggregation(
                                                        AggregationBuilders
                                                                .terms("statusinner")
                                                                .field("statusinner")
                                                )
                                )
                );
        System.out.println("查询参数：--"+sbuilder);
        SearchResponse sr = sbuilder.execute().actionGet();
        //System.out.println("输出结果：--"+sr);
        Terms userid = sr.getAggregations().get("userid");
        // For each entry
        for (Terms.Bucket entry : userid.getBuckets())
        {
            String key = (String) entry.getKey();
            System.out.println("serviceidnorth :--"+key+" count:---"+ entry.getDocCount());
            Terms appkey = entry.getAggregations().get("serviceidnorth");
            for (Terms.Bucket pairs : appkey.getBuckets())
            {
                String appKey = (String) pairs.getKey();
                System.out.println("       serviceidnorth :--"+appKey+" count:---"+ pairs.getDocCount());
                Terms statusinner = pairs.getAggregations().get("statusinner");
                if (statusinner!=null)
                {
                    for (Terms.Bucket pp:statusinner.getBuckets())
                    {
                        Long  name = (Long)pp.getKey();
                        System.out.println("            name:--"+name+"  count:--"+pp.getDocCount());
                    }
                }

            }
        }
        //ClientUtils.closeClient(client);
        return null;
    }


    /**
     * 某个索引库中，某个
     * @param indeces
     * @param field
     * @param value
     * @return
     * @throws Exception
     */
    public List<ServiceInfor> searchUsersServices(String indeces,String field,String value) throws Exception
    {
        Client client = ClientUtils.getDefaultElasticSearchClient();
        /**
         * 获得不同用户，不同服务，不同时间段 的调用次数，
         */
        SearchRequestBuilder sbuilder = client
                .prepareSearch(indeces)
                .setQuery(
                        QueryBuilders.matchQuery(field, value)
                )
                .addAggregation(
                        AggregationBuilders
                                .terms("userid")
                                .field("userid.raw")
                                .subAggregation(
                                        AggregationBuilders
                                                .terms("serviceidnorth")
                                                .field("serviceidnorth.raw")
                                                .subAggregation(
                                                        AggregationBuilders
                                                                .terms("statusinner")
                                                                .field("statusinner")
                                                )
                                )
                );
        System.out.println("查询参数：--"+sbuilder);
        SearchResponse sr = sbuilder.execute().actionGet();
        //System.out.println("输出结果：--"+sr);
        Terms userid = sr.getAggregations().get("userid");
        // For each entry
        for (Terms.Bucket entry : userid.getBuckets())
        {
            String key = (String) entry.getKey();
            System.out.println("serviceidnorth :--"+key+" count:---"+ entry.getDocCount());
            Terms appkey = entry.getAggregations().get("serviceidnorth");
            for (Terms.Bucket pairs : appkey.getBuckets())
            {
                String appKey = (String) pairs.getKey();
                System.out.println("       serviceidnorth :--"+appKey+" count:---"+ pairs.getDocCount());
                Terms statusinner = pairs.getAggregations().get("statusinner");
                if (statusinner!=null)
                {
                    for (Terms.Bucket pp:statusinner.getBuckets())
                    {
                        Long  name = (Long)pp.getKey();
                        System.out.println("            name:--"+name+"  count:--"+pp.getDocCount());
                    }
                }

            }
        }
       // ClientUtils.closeClient(client);
        return null;
    }

    /**
     * 通过from and size的方式来实现分页
     * @param indeces       ：库
     * @param type          ：表
     * @return
     * @throws Exception
     */
    public List<ServiceInfor> getUsersServiceByUserIdByFromAndSize(String indeces,String type) throws Exception
    {
        Client client = ClientUtils.getDefaultElasticSearchClient();

        System.out.println("from size 模式启动！");
        Date begin = new Date();
        //查询出所有的记录条数
        long count = client.prepareCount(indeces).setTypes(type).execute().actionGet().getCount();
        SearchRequestBuilder requestBuilder = client.prepareSearch(indeces).setTypes(type).setQuery(QueryBuilders.matchAllQuery());
        System.out.println("总记录条数：-"+count);
        for(int i=0,sum=0; sum<count; i++)
        {
            SearchResponse response = requestBuilder.setFrom(i).setSize(100).execute().actionGet();
            sum += response.getHits().hits().length;
            System.out.println("总量"+count+" 已经查到"+sum);
        }
        Date end = new Date();
        System.out.println("耗时: "+(end.getTime()-begin.getTime()));
        ///ClientUtils.closeClient(client);
        return null;
    }

    /**
     * 通过Scroll的方式实现分页
     * @param indeces   ：库
     * @param type         ：类型
     * @return
     * @throws Exception
     */
    public List<ServiceInfor> getUsersServiceByUserIdByScroll(String indeces,String type) throws Exception
    {
        Client client = ClientUtils.getDefaultElasticSearchClient();

        System.out.println("scroll 模式启动！");
        Date begin = new Date();
        SearchResponse scrollResponse = client.prepareSearch(indeces)
                .setSearchType(SearchType.SCAN).setSize(10000).setScroll(TimeValue.timeValueMinutes(1))
                .execute().actionGet();
       long  count = scrollResponse.getHits().getTotalHits();//第一次不返回数据
        System.out.println("总记录条数："+count);
        for(int i=0,sum=0; sum<count; i++){
            scrollResponse = client.prepareSearchScroll(scrollResponse.getScrollId())
                    .setScroll(TimeValue.timeValueMinutes(8))
                    .execute().actionGet();
            sum += scrollResponse.getHits().hits().length;
            System.out.println("总量"+count+" 已经查到"+sum);
        }
       Date end = new Date();
        System.out.println("耗时: "+(end.getTime()-begin.getTime()));
       // ClientUtils.closeClient(client);
        return null;
    }

    /**
     * 条件查询
     * @param indeces
     * @param trackId
     * @throws Exception
     */
    public void searchByTrackId(String indeces,String trackId)throws  Exception
    {
        Client client = ClientUtils.getDefaultElasticSearchClient();

        SearchRequestBuilder  sbuilder = client.prepareSearch(indeces) //index name
                .setTypes("servicelog") //type name
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.termQuery("trackid.raw", "298d198d-aa60-48af-a9f4-638f8f274afa"))                // Query
                .setExplain(true);
        System.out.println(" 查询参数："+sbuilder.toString());
        SearchResponse response = sbuilder.execute().actionGet();
        System.out.println(" 返回参数："+response.toString());
        SearchHits hits = response.getHits();
        System.out.println("返回结果："+response);
        System.out.println("查询到记录数=" + hits.getTotalHits());
        SearchHit[] searchHists = hits.getHits();
        if(searchHists.length>0){
            for(SearchHit hit:searchHists)
            {
                Long inrecords =  (Long)hit.getSource().get("servicecount.inrecords");
                Long outrecords = (Long)hit.getSource().get("servicecount.outrecords");
                System.out.println("输入记录："+inrecords+" 输出记录："+outrecords);
            }
        }
        //ClientUtils.closeClient(client);
    }
}
