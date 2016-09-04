package com.talkingdata.dao;

import com.talkingdata.domain.*;
import com.talkingdata.utils.ClientUtils;
import com.talkingdata.utils.DateUtils;
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
 * 我的账户界面提供的的Dao层
 * User：    ysl
 * Date:   2016/7/5
 * Time:   17:08
 */
public class MyAccountDao {

  /*  public static void main(String[] args)
    {
        MyAccountDao myAccountDao = new MyAccountDao();
        try{
            User user = myAccountDao.getUserServices("logstash-gatewaylog",null,"gatewaylog","15236282835@163.com",0l,0l);
            if(user!=null)
            {
                List<Appkey> appkeys = user.getAppkeys();

                if(appkeys!=null&&appkeys.size()>0)
                {
                    for (Appkey appkey: appkeys)
                    {
                        //System.out.println("     appkey"+appkey.getAppkey());
                        List<ServiceInfor> serviceInforList = appkey.getServiceInforList();
                        System.out.print(serviceInforList.size());
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
                                        System.out.println(servicelog.getInRecords());
                                        inRecordsSum +=servicelog.getInRecords();
                                        outRecordsSum +=servicelog.getOutRecords();
                                        // measureDao.statisticByTracekId("logstash-servicelog", "servicelog", servicelog);
                                        System.out.println("              该服务调用的trackid:" + servicelog.getTrackId());
                                    }
                                    serviceInfor.setInRecordsSum(inRecordsSum);
                                    serviceInfor.setOutRecordsSum(outRecordsSum);
                                    System.out.println("           输入记录数：" + inRecordsSum + " 输出记录数：" + outRecordsSum);
                                }
                            }
                        }
                    }
                }else
                {
                }
            }else
            {
            }
        }catch (Exception e)
        {
            System.out.print("程序异常！！");
        }

    }*/
    public static void main(String[] args)
    {
        MyAccountDao myAccountDao = new MyAccountDao();
        try {
            long startTimeMillions = DateUtils.dateToMillions(DateUtils.strToDate("2016-07-21 00:00:00"));
            long endTimeMillions = DateUtils.currentMillions();
            List<WeekReport> weekReports = myAccountDao.getWeekServicesUsedInfor("logstash-gatewaylog", "gatewaylog", "xue.li@tendcloud.com", "8aefa8bb602911e6b2250205857feb80",
                    startTimeMillions,endTimeMillions ) ;
            System.out.println(weekReports.size());
            for(WeekReport weekReport:weekReports)
            {
                System.out.println(weekReport.getAccessRequest());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 某个索引库中，某个用户的所有记录数据
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
        ClientUtils.closeClient(client);
        return null;
    }

    /**
     *根据用户名称、起始时间、结束时间来
     *      查询该用户的服务调用情况
     * @param indeces           :索引库
     * @param type              ：类型
     * @param userName          ：用户名称
     * @param startTimeMillions     ：起始时间
     * @param endTimeMillions          ：结束时间
     * @return
     * @throws Exception
     */
    public User getUserServices(String indeces,String type,String userName,String serviceIdNorth,long startTimeMillions,long endTimeMillions)throws Exception
    {
        MeasureDao measureDao = new MeasureDao();
        User user = new User();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(
                        QueryBuilders.termQuery("username.raw", userName)
                );
        if (!StringUtils.isEmpty(serviceIdNorth))
        {
            System.out.println(serviceIdNorth);
            boolQuery.must(
                    QueryBuilders.termQuery("serviceidnorth.raw", serviceIdNorth)
            );
        }
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
                                .terms("appkey")
                                .field("appkey.raw")        //服务标识索引
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
        Terms appkeys = sr.getAggregations().get("appkey");
        // For each entry
        if(appkeys!=null)
        {
            List<Appkey> appkeyList = new ArrayList<Appkey>();
            for (Terms.Bucket entry : appkeys.getBuckets())
            {
                Appkey ak = new Appkey();
                String appkey = (String) entry.getKey();
                ak.setAppkey(appkey);
                //System.out.println("appkey :--" + appkey + " count:---" + entry.getDocCount());
                Terms serviceidnorth = entry.getAggregations().get("serviceidnorth");
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
                appkeyList.add(ak);
            }
            user.setAppkeys(appkeyList);
        }
        //关闭客户端
        ClientUtils.closeClient(client);
        return user;
    }

    /**
     *通过用户id、或者再加上服务id来查询服务的调用情况
     * @param indeces                   :索引库
     * @param type                      ：类型
     * @param userId                    ：用户id
     * @param serviceId               :服务id     （选填）
     * @param startTimeMillions         ：起始时间戳
     * @param endTimeMillions           ：结束时间戳
     * @return
     * @throws Exception
     */
    public List<WeekReport> getWeekServicesUsedInfor(String indeces ,String type,
                                                     String userId,
                                                     String serviceId,
                                                     long startTimeMillions,
                                                     long endTimeMillions )throws Exception
    {
        List<WeekReport> weekReports = new ArrayList<WeekReport>();
        //获得客户端
        Client client = ClientUtils.getDefaultElasticSearchClient();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(
                        QueryBuilders.termQuery("userid.raw", userId)                   //用户id
                );
        if (!StringUtils.isEmpty(serviceId))
        {
            boolQuery.must(
                    QueryBuilders.termQuery("serviceid.raw", serviceId)                 //服务字段
            );
        }
        if(startTimeMillions!=0l&&endTimeMillions!=0l)
        {
            boolQuery.must(
                    QueryBuilders.rangeQuery("startts")                     //时间字段
                            .from(startTimeMillions)
                            .to(endTimeMillions)
            );
        }
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
                                        AggregationBuilders
                                                .terms("statusinner")       //成功调用次数
                                                .field("statusinner")
                                                .subAggregation(
                                                        AggregationBuilders
                                                                .sum("inputCount")       //输入记录
                                                                .field("inputCount")
                                                )
                                                .subAggregation(
                                                        AggregationBuilders
                                                                .sum("outputCount")         //输出记录
                                                                .field("outputCount")
                                                )
                                )
                );
        System.out.println("查询参数：--"+sbuilder);
        SearchResponse sr = sbuilder.execute().actionGet();
        //System.out.println("输出结果：--"+sr);
        Terms serviceids = sr.getAggregations().get("serviceid");
        // For each entry
        if(serviceids!=null)
        {
            for (Terms.Bucket entry : serviceids.getBuckets())
            {
                WeekReport wr = new WeekReport();
                String servic_id = (String) entry.getKey();
                wr.setServiceId(servic_id);
                //System.out.println("serviceId :--" + servic_id + " count:---" + entry.getDocCount());
                wr.setAccessRequest(entry.getDocCount());
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
                            wr.setSuccessRequest(successRequest);
                            Sum inputCount = pairs.getAggregations().get("inputCount");
                            Sum outputCount = pairs.getAggregations().get("outputCount");
                            wr.setInputCount((long) inputCount.getValue());
                            wr.setOututCount((long) outputCount.getValue());
                             //System.out.println("       inputCount :--" + inputCount.getValue() + " outputCount:---" + outputCount.getValue());
                        }
                    }
                }
                weekReports.add(wr);
            }
        }
        //关闭客户端
        //ClientUtils.closeClient(client);
        return weekReports;
    }

    /**
     *查询用户，某服务，某天内的调用情况
     * @param indeces                   ：索引库
     * @param type                      ：类型
     * @param userId                    ：用户id
     * @param serviceId                 ：服务id
     * @param startTimeMillions         ：起始时间戳
     * @param endTimeMillions           ：结束时间戳
     * @return
     * @throws Exception
     */
    public DayReport getDayServicesUsedInfor(String indeces ,String type,
                                                     String userId,
                                                     String serviceId,
                                                     long startTimeMillions,
                                                     long endTimeMillions )throws Exception
    {
        DayReport dayReport = new DayReport();
        //获得客户端
        Client client = ClientUtils.getDefaultElasticSearchClient();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(
                        QueryBuilders.termQuery("userid.raw", userId)                   //用户id
                );
        if (!StringUtils.isEmpty(serviceId))
        {
            boolQuery.must(
                    QueryBuilders.termQuery("serviceid.raw", serviceId)                 //服务字段
            );
        }
        if(startTimeMillions!=0l&&endTimeMillions!=0l)
        {
            boolQuery.must(
                    QueryBuilders.rangeQuery("startts")                     //时间字段
                            .from(startTimeMillions)
                            .to(endTimeMillions)
            );
        }
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
                                        AggregationBuilders
                                                .terms("statusinner")       //成功调用次数
                                                .field("statusinner")
                                                .subAggregation(
                                                        AggregationBuilders
                                                                .sum("inputCount")       //输入记录
                                                                .field("inputCount")
                                                )
                                                .subAggregation(
                                                        AggregationBuilders
                                                                .sum("outputCount")         //输出记录
                                                                .field("outputCount")
                                                )
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
                String servic_id = (String) entry.getKey();
                //System.out.println("serviceId :--" + servic_id + " count:---" + entry.getDocCount());
                dayReport.setAccessRequest(entry.getDocCount());
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
                            dayReport.setSuccessRequest(successRequest);
                            Sum inputCount = pairs.getAggregations().get("inputCount");
                            Sum outputCount = pairs.getAggregations().get("outputCount");
                            dayReport.setInputCount((long) inputCount.getValue());
                            dayReport.setOututCount((long) outputCount.getValue());
                            //System.out.println("       inputCount :--" + inputCount.getValue() + " outputCount:---" + outputCount.getValue());
                        }
                    }
                }
            }
        }
        return dayReport;
    }


}
