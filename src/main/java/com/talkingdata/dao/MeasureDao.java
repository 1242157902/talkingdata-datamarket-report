package com.talkingdata.dao;

import com.talkingdata.domain.MyServiceInfor;
import com.talkingdata.domain.ServiceInfor;
import com.talkingdata.domain.Servicelog;
import com.talkingdata.utils.ClientUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 为计量管理界面提供服务的Dao层
 * User：    ysl
 * Date:   2016/7/5
 * Time:   16:24
 */
public class MeasureDao {

    //获得客户端




    public static void main(String[] args)
    {
        MeasureDao measureDao = new MeasureDao();
        try{
            //List<MyServiceInfor> myServiceInfors = measureDao.getUsersServices("logstash-gatewaylog");
           // List<MyServiceInfor> myServiceInfors = measureDao.getTimeServices("logstash-gatewaylog", "servicelog",1466583333753l,1466583333761l);
           // List<MyServiceInfor> myServiceInfors = measureDao.statisticByUserId("logstash-gatewaylog", "servicelog","marketUser2");
           //List<MyServiceInfor> myServiceInfors = measureDao.statisticByUserIdAndTime("logstash-gatewaylog", "servicelog", "marketUser2",1466583333753l, 1466583333756l);
           List<MyServiceInfor> myServiceInfors = measureDao.statisticByUserIdAndTimeAndServiceId("logstash-gatewaylog", "servicelog", "marketUser2", "dmp/devinfo/v1", 1466583333753l, 1466583333756l);
            System.out.print(myServiceInfors.size());
            for(MyServiceInfor myServiceInfor:myServiceInfors)
            {
                System.out.println(" 用户名称："+myServiceInfor.getUserName());
                List<ServiceInfor> serviceInforList = myServiceInfor.getServiceInforList();
                for(ServiceInfor serviceInfor:serviceInforList)
                {
                    System.out.println("         服务名称："+serviceInfor.getServiceNorth()+
                                                "  调用次数："+serviceInfor.getAccessNumber()+
                                                 " 成功次数："+serviceInfor.getSuccessNumber()+
                                                 " 失败次数："+serviceInfor.getFailedNumber());
                    Set<Servicelog> servicelogs = serviceInfor.getServicelogs();
                    if(servicelogs!=null&&servicelogs.size()>0)
                    {
                        for(Servicelog servicelog:servicelogs)
                        {
                             System.out.println("              该服务调用输入记录数:" + servicelog.getInRecords()+
                                                                "  输出记录数："+servicelog.getOutRecords());
                        }
                    }
                }
            }

            /*Servicelog servicelog = new Servicelog();
            servicelog.setTrackId("d66e8806-588a-4a1d-9578-43e5104bafd3");
            measureDao.statisticByTracekId("logstash-servicelog","servicelog",servicelog);*/

        }catch (Exception e)
        {
            System.out.println("程序发生异常！");
            e.printStackTrace();
        }
    }


    /**
     * 需要获得不同用户、不同服务的调用次数
     * @param indeces       ：索引库
     * @return
     * @throws Exception
     */
    public List<MyServiceInfor> getUsersServices(String indeces) throws Exception
    {
        Client client = ClientUtils.getDefaultElasticSearchClient();
        List<MyServiceInfor> myServiceInforList = new ArrayList<MyServiceInfor>();
        SearchRequestBuilder sbuilder = client
                .prepareSearch(indeces)
                .setQuery(
                        QueryBuilders.matchAllQuery()       //匹配所有的记录
                )
                .addAggregation(
                        AggregationBuilders
                                .terms("userid")
                                .field("userid.raw")        //用户标识索引
                                .subAggregation(
                                        AggregationBuilders
                                                .terms("serviceidnorth")
                                                .field("serviceidnorth.raw")        //服务索引
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
        Terms userid = sr.getAggregations().get("userid");
        // For each entry
        if(userid!=null)
        {
            for (Terms.Bucket entry : userid.getBuckets())
            {
                MyServiceInfor myServiceInfor = new MyServiceInfor();
                String userName = (String) entry.getKey();
                myServiceInfor.setUserName(userName);
               //System.out.println("userd :--"+userName+" count:---"+ entry.getDocCount());
                Terms serviceidnorth = entry.getAggregations().get("serviceidnorth");
                if(serviceidnorth!=null)
                {
                    List<ServiceInfor> serviceInforList = new ArrayList<ServiceInfor>();
                    for (Terms.Bucket pairs : serviceidnorth.getBuckets())
                    {
                        ServiceInfor serviceInfor = new ServiceInfor();
                        String serviceIdNorth = (String) pairs.getKey();
                        long userNumber = pairs.getDocCount();
                        serviceInfor.setServiceNorth(serviceIdNorth);
                        serviceInfor.setAccessNumber(userNumber);
                       // System.out.println("       serviceidnorth :--"+serviceIdNorth+" count:---"+userNumber );
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
                            serviceInfor.setFailedNumber(userNumber-serviceInfor.getSuccessNumber());
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
                               servicelogs.add(statisticByTracekId("logstash-servicelog","servicelog",servicelog));//trackid没有重复的情况下可以使用
                               // servicelogs.add(servicelog);
                            }
                            serviceInfor.setServicelogs(servicelogs);
                        }
                        serviceInforList.add(serviceInfor);
                    }
                    myServiceInfor.setServiceInforList(serviceInforList);
                }

                myServiceInforList.add(myServiceInfor);
            }

        }
        //关闭客户端
       // ClientUtils.closeClient(client);
        return myServiceInforList;
    }

    /**
     * 每一种服务，又会有多个trackid，然后针对每个trackId来聚合所要输出记录数、输出记录数。
     *通过trackId来，聚合输入记录、输出记录
     * @param indeces           :索引库
     * @param type              ：库中的类型
     * @param servicelog        ：servicelog日志
     * @return
     * @throws Exception
     */
    public Servicelog  statisticByTracekId(String indeces,String type ,Servicelog servicelog) throws Exception
    {
        if(servicelog!=null)
        {
           String trackId = servicelog.getTrackId();
            if(trackId!=null)
            {
                Client client =  ClientUtils.getDefaultElasticSearchClient();
                SearchRequestBuilder sbuilder = client
                        .prepareSearch(indeces)
                        .setQuery(
                                QueryBuilders.termQuery("trackid.raw", trackId)

                        )
                        .addAggregation(
                                AggregationBuilders
                                        .sum("inrecordsSum")
                                        .field("servicecount.inrecords")
                        )
                        .addAggregation(
                                AggregationBuilders
                                        .sum("outrecordsSum")
                                        .field("servicecount.outrecords")
                        );
                //System.out.println("查询参数：--"+sbuilder);
                SearchResponse sr = sbuilder.execute().actionGet();
                // System.out.println("输出结果：--"+sr);
                Sum inrecordsSum = sr.getAggregations().get("inrecordsSum");
                Sum outrecordsSum = sr.getAggregations().get("outrecordsSum");
                if(inrecordsSum!=null)
                {
                    servicelog.setInRecords((long)inrecordsSum.getValue());
                }
                if(outrecordsSum!=null)
                {
                    servicelog.setOutRecords((long)outrecordsSum.getValue());
                }
              /*System.out.println("        trackId:"+trackId+
                    "  输入记录数："+inrecordsSum.getValue()
                    +" 输出记录数："+outrecordsSum.getValue());*/
               // ClientUtils.closeClient(client);
            }
            return servicelog;
        }
        else
        {
            return null;
        }

    }

    /**
     *
     * @param indeces                       :索引库
     * @param type                          ：类型名
     * @param startTimeMillions             ：起始时间
     * @param endTimeMillions               ：结束时间
     * @return                              ：返回List<MyServiceInfor>类型
     * @throws Exception
     */
    public List<MyServiceInfor> getTimeServices(String indeces ,String type,long startTimeMillions,long endTimeMillions )throws Exception
    {
        List<MyServiceInfor> myServiceInforList = new ArrayList<MyServiceInfor>();
        //获得客户端
        Client client = ClientUtils.getDefaultElasticSearchClient();
        SearchRequestBuilder sbuilder = client
                .prepareSearch(indeces)
                .setQuery(
                        QueryBuilders.rangeQuery("startts")                     //时间字段
                                .from(startTimeMillions)
                                .to(endTimeMillions)
                )
                .addAggregation(
                        AggregationBuilders
                                .terms("userid")
                                .field("userid.raw")        //用户标识索引
                                .subAggregation(
                                        AggregationBuilders
                                                .terms("serviceidnorth")
                                                .field("serviceidnorth.raw")        //服务索引
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
        Terms userid = sr.getAggregations().get("userid");
        // For each entry
        if(userid!=null)
        {
            for (Terms.Bucket entry : userid.getBuckets())
            {
                MyServiceInfor myServiceInfor = new MyServiceInfor();
                String userName = (String) entry.getKey();
                myServiceInfor.setUserName(userName);
                //System.out.println("userd :--"+userName+" count:---"+ entry.getDocCount());
                Terms serviceidnorth = entry.getAggregations().get("serviceidnorth");
                if(serviceidnorth!=null)
                {
                    List<ServiceInfor> serviceInforList = new ArrayList<ServiceInfor>();
                    for (Terms.Bucket pairs : serviceidnorth.getBuckets())
                    {
                        ServiceInfor serviceInfor = new ServiceInfor();
                        String serviceIdNorth = (String) pairs.getKey();
                        long userNumber = pairs.getDocCount();
                        serviceInfor.setServiceNorth(serviceIdNorth);
                        serviceInfor.setAccessNumber(userNumber);
                        // System.out.println("       serviceidnorth :--"+serviceIdNorth+" count:---"+userNumber );
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
                            serviceInfor.setFailedNumber(userNumber-serviceInfor.getSuccessNumber());
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
                                servicelogs.add(statisticByTracekId("logstash-servicelog","servicelog",servicelog));//trackid没有重复的情况下可以使用
                                // servicelogs.add(servicelog);
                            }
                            serviceInfor.setServicelogs(servicelogs);
                        }
                        serviceInforList.add(serviceInfor);
                    }
                    myServiceInfor.setServiceInforList(serviceInforList);
                }

                myServiceInforList.add(myServiceInfor);
            }

        }
        //关闭客户端
        //ClientUtils.closeClient(client);
        return myServiceInforList;
    }

    /**
     * 查询各个服务的调用情况
     * @param indeces           ：索引库
     * @param type              ：类型
     * @param userId            ：用户id
     * @return                  ：List<MyServiceInfor>
     * @throws Exception
     */
    public List<MyServiceInfor> statisticByUserId(String indeces,String type,String userId) throws Exception
    {
        List<MyServiceInfor> myServiceInforList = new ArrayList<MyServiceInfor>();
        //获得客户端
        Client client = ClientUtils.getDefaultElasticSearchClient();
        SearchRequestBuilder sbuilder = client
                .prepareSearch(indeces)
                .setQuery(
                        QueryBuilders.termQuery("userid.raw", userId)     //匹配所有的记录
                )
                .addAggregation(
                        AggregationBuilders
                                .terms("userid")
                                .field("userid.raw")        //用户标识索引
                                .subAggregation(
                                        AggregationBuilders
                                                .terms("serviceidnorth")
                                                .field("serviceidnorth.raw")        //服务索引
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
        Terms userid = sr.getAggregations().get("userid");
        // For each entry
        if(userid!=null)
        {
            for (Terms.Bucket entry : userid.getBuckets())
            {
                MyServiceInfor myServiceInfor = new MyServiceInfor();
                String userName = (String) entry.getKey();
                myServiceInfor.setUserName(userName);
                //System.out.println("userd :--"+userName+" count:---"+ entry.getDocCount());
                Terms serviceidnorth = entry.getAggregations().get("serviceidnorth");
                if(serviceidnorth!=null)
                {
                    List<ServiceInfor> serviceInforList = new ArrayList<ServiceInfor>();
                    for (Terms.Bucket pairs : serviceidnorth.getBuckets())
                    {
                        ServiceInfor serviceInfor = new ServiceInfor();
                        String serviceIdNorth = (String) pairs.getKey();
                        long userNumber = pairs.getDocCount();
                        serviceInfor.setServiceNorth(serviceIdNorth);
                        serviceInfor.setAccessNumber(userNumber);
                        // System.out.println("       serviceidnorth :--"+serviceIdNorth+" count:---"+userNumber );
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
                            serviceInfor.setFailedNumber(userNumber-serviceInfor.getSuccessNumber());
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
                                servicelogs.add(statisticByTracekId("logstash-servicelog","servicelog",servicelog));//trackid没有重复的情况下可以使用
                                // servicelogs.add(servicelog);
                            }
                            serviceInfor.setServicelogs(servicelogs);
                        }
                        serviceInforList.add(serviceInfor);
                    }
                    myServiceInfor.setServiceInforList(serviceInforList);
                }

                myServiceInforList.add(myServiceInfor);
            }

        }
        //关闭客户端
       // ClientUtils.closeClient(client);
        return myServiceInforList;
    }

    /**
     * 通过用户id，serviceid来查询服务的调用情况
     * @param indeces           :所以库
     * @param type              ：类型
     * @param userId            ：用户id
     * @param serviceId         ：服务id
     * @return
     * @throws Exception
     */
    public List<MyServiceInfor> statisticByUserIdAndServiceId(String indeces,String type,String userId,String serviceId) throws Exception
    {
        List<MyServiceInfor> myServiceInforList = new ArrayList<MyServiceInfor>();
        //获得客户端
        Client client = ClientUtils.getDefaultElasticSearchClient();
        SearchRequestBuilder sbuilder = client
                .prepareSearch(indeces)
                .setQuery(
                        QueryBuilders.boolQuery()
                                .must(
                                        QueryBuilders.termQuery("userid.raw", userId)
                                )
                                .must(
                                        QueryBuilders.termQuery("serviceidnorth.raw", serviceId)
                                )
                )
                .addAggregation(
                        AggregationBuilders
                                .terms("userid")
                                .field("userid.raw")        //用户标识索引
                                .subAggregation(
                                        AggregationBuilders
                                                .terms("serviceidnorth")
                                                .field("serviceidnorth.raw")        //服务索引
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
        Terms userid = sr.getAggregations().get("userid");
        // For each entry
        if(userid!=null)
        {
            for (Terms.Bucket entry : userid.getBuckets())
            {
                MyServiceInfor myServiceInfor = new MyServiceInfor();
                String userName = (String) entry.getKey();
                myServiceInfor.setUserName(userName);
                //System.out.println("userd :--"+userName+" count:---"+ entry.getDocCount());
                Terms serviceidnorth = entry.getAggregations().get("serviceidnorth");
                if(serviceidnorth!=null)
                {
                    List<ServiceInfor> serviceInforList = new ArrayList<ServiceInfor>();
                    for (Terms.Bucket pairs : serviceidnorth.getBuckets())
                    {
                        ServiceInfor serviceInfor = new ServiceInfor();
                        String serviceIdNorth = (String) pairs.getKey();
                        long userNumber = pairs.getDocCount();
                        serviceInfor.setServiceNorth(serviceIdNorth);
                        serviceInfor.setAccessNumber(userNumber);
                        // System.out.println("       serviceidnorth :--"+serviceIdNorth+" count:---"+userNumber );
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
                            serviceInfor.setFailedNumber(userNumber-serviceInfor.getSuccessNumber());
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
                                servicelogs.add(statisticByTracekId("logstash-servicelog","servicelog",servicelog));//trackid没有重复的情况下可以使用
                                // servicelogs.add(servicelog);
                            }
                            serviceInfor.setServicelogs(servicelogs);
                        }
                        serviceInforList.add(serviceInfor);
                    }
                    myServiceInfor.setServiceInforList(serviceInforList);
                }

                myServiceInforList.add(myServiceInfor);
            }

        }
        //关闭客户端
        ///ClientUtils.closeClient(client);
        return myServiceInforList;
    }

    /**
     * 查询某一用户，某段时间内服务的调用情况
     * @param indeces                       :索引库
     * @param type                          ：类型
     * @param userId                        ：用户id
     * @param startTimeMillions             ：起始时间
     * @param endTimeMillions               ：结束时间
     * @return
     * @throws Exception
     */
    public List<MyServiceInfor> statisticByUserIdAndTime(String indeces,String type,String userId,long startTimeMillions,long endTimeMillions) throws Exception
    {
        List<MyServiceInfor> myServiceInforList = new ArrayList<MyServiceInfor>();
        //获得客户端
        Client client = ClientUtils.getDefaultElasticSearchClient();
        SearchRequestBuilder sbuilder = client
                .prepareSearch(indeces)
                .setQuery(
                        QueryBuilders.boolQuery()
                                .must(
                                        QueryBuilders.termQuery("userid.raw", userId)
                                )
                                .must(
                                        QueryBuilders.rangeQuery("startts")
                                                .from(startTimeMillions)
                                                .to(endTimeMillions)
                                )
                )
                .addAggregation(
                        AggregationBuilders
                                .terms("userid")
                                .field("userid.raw")        //用户标识索引
                                .subAggregation(
                                        AggregationBuilders
                                                .terms("serviceidnorth")
                                                .field("serviceidnorth.raw")        //服务索引
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
        Terms userid = sr.getAggregations().get("userid");
        // For each entry
        if(userid!=null)
        {
            for (Terms.Bucket entry : userid.getBuckets())
            {
                MyServiceInfor myServiceInfor = new MyServiceInfor();
                String userName = (String) entry.getKey();
                myServiceInfor.setUserName(userName);
                //System.out.println("userd :--"+userName+" count:---"+ entry.getDocCount());
                Terms serviceidnorth = entry.getAggregations().get("serviceidnorth");
                if(serviceidnorth!=null)
                {
                    List<ServiceInfor> serviceInforList = new ArrayList<ServiceInfor>();
                    for (Terms.Bucket pairs : serviceidnorth.getBuckets())
                    {
                        ServiceInfor serviceInfor = new ServiceInfor();
                        String serviceIdNorth = (String) pairs.getKey();
                        long userNumber = pairs.getDocCount();
                        serviceInfor.setServiceNorth(serviceIdNorth);
                        serviceInfor.setAccessNumber(userNumber);
                        // System.out.println("       serviceidnorth :--"+serviceIdNorth+" count:---"+userNumber );
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
                            serviceInfor.setFailedNumber(userNumber-serviceInfor.getSuccessNumber());
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
                                servicelogs.add(statisticByTracekId("logstash-servicelog","servicelog",servicelog));//trackid没有重复的情况下可以使用
                                // servicelogs.add(servicelog);
                            }
                            serviceInfor.setServicelogs(servicelogs);
                        }
                        serviceInforList.add(serviceInfor);
                    }
                    myServiceInfor.setServiceInforList(serviceInforList);
                }

                myServiceInforList.add(myServiceInfor);
            }

        }
        //关闭客户端
        //ClientUtils.closeClient(client);
        return myServiceInforList;
    }

    /**
     * 查询某一用户，某一服务，某段时间内的服务的调用情况
     * @param indeces           :索引库
     * @param type              ：类型
     * @param userId            ：用户id
     * @param serviceId         ：服务id
     * @param startTimeMillions ：起始时间
     * @param endTimeMillions   ：结束时间
     * @return                      ：服务的调用情况
     * @throws Exception
     */
    public List<MyServiceInfor> statisticByUserIdAndTimeAndServiceId(String indeces,String type,String userId,String serviceId,long startTimeMillions,long endTimeMillions) throws Exception
    {
        List<MyServiceInfor> myServiceInforList = new ArrayList<MyServiceInfor>();
        //获得客户端
        Client client = ClientUtils.getDefaultElasticSearchClient();
        SearchRequestBuilder sbuilder = client
                .prepareSearch(indeces)
                .setQuery(
                        QueryBuilders.boolQuery()
                                .must(
                                        QueryBuilders.termQuery("userid.raw", userId)
                                )
                                .must(
                                        QueryBuilders.rangeQuery("startts")
                                                .from(startTimeMillions)
                                                .to(endTimeMillions)
                                )
                                .must(
                                        QueryBuilders.termQuery("serviceidnorth.raw", serviceId)
                                )
                )
                .addAggregation(
                        AggregationBuilders
                                .terms("userid")
                                .field("userid.raw")        //用户标识索引
                                .subAggregation(
                                        AggregationBuilders
                                                .terms("serviceidnorth")
                                                .field("serviceidnorth.raw")        //服务索引
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
        Terms userid = sr.getAggregations().get("userid");
        // For each entry
        if(userid!=null)
        {
            for (Terms.Bucket entry : userid.getBuckets())
            {
                MyServiceInfor myServiceInfor = new MyServiceInfor();
                String userName = (String) entry.getKey();
                myServiceInfor.setUserName(userName);
                //System.out.println("userd :--"+userName+" count:---"+ entry.getDocCount());
                Terms serviceidnorth = entry.getAggregations().get("serviceidnorth");
                if(serviceidnorth!=null)
                {
                    List<ServiceInfor> serviceInforList = new ArrayList<ServiceInfor>();
                    for (Terms.Bucket pairs : serviceidnorth.getBuckets())
                    {
                        ServiceInfor serviceInfor = new ServiceInfor();
                        String serviceIdNorth = (String) pairs.getKey();
                        long userNumber = pairs.getDocCount();
                        serviceInfor.setServiceNorth(serviceIdNorth);
                        serviceInfor.setAccessNumber(userNumber);
                        // System.out.println("       serviceidnorth :--"+serviceIdNorth+" count:---"+userNumber );
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
                            serviceInfor.setFailedNumber(userNumber-serviceInfor.getSuccessNumber());
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
                                servicelogs.add(statisticByTracekId("logstash-servicelog","servicelog",servicelog));//trackid没有重复的情况下可以使用
                                // servicelogs.add(servicelog);
                            }
                            serviceInfor.setServicelogs(servicelogs);
                        }
                        serviceInforList.add(serviceInfor);
                    }
                    myServiceInfor.setServiceInforList(serviceInforList);
                }

                myServiceInforList.add(myServiceInfor);
            }

        }
        //关闭客户端
       // ClientUtils.closeClient(client);
        return myServiceInforList;
    }



}
