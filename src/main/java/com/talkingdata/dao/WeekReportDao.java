package com.talkingdata.dao;

import com.talkingdata.domain.MyServiceInfor;
import com.talkingdata.domain.ServiceInfor;
import com.talkingdata.domain.WeekReport;
import com.talkingdata.utils.ClientUtils;
import com.talkingdata.utils.DateUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用于每周为leader发送每周的业务数据
 * User：    ysl
 * Date:   2016/7/28
 * Time:   18:03
 */
public class WeekReportDao {

    public static void main(String[] args)
    {
        WeekReportDao wpd = new WeekReportDao();
        long startTimeMillions = DateUtils.dateToMillions(DateUtils.strToDate("2016-08-08 09:20:00"));
        long endTimeMillions = DateUtils.dateToMillions(DateUtils.strToDate("2016-08-10 23:59:59"));
        try {
            List<WeekReport> weekReports =  wpd.getWeekServicesUsedInfor("logstash-gatewaylog", "gatewaylog",
                    startTimeMillions, endTimeMillions);
            for(WeekReport weekReport:weekReports)
            {
                System.out.println(" seviceId:"+weekReport.getServiceId()+
                                    " 成功请求次数："+weekReport.getSuccessRequest());
            }

        } catch (Exception e) {
            System.out.println("程序发生异常！");
            e.printStackTrace();
        }
    }


    /**
     *用于为leader发送每天的业务数据报表
     * @param indeces                       :索引库
     * @param type                          ：类型名
     * @param startTimeMillions             ：起始时间
     * @param endTimeMillions               ：结束时间
     * @return                              ：返回List<WeekReport>类型
     * @throws Exception
     */
    public List<WeekReport> getWeekServicesUsedInfor(String indeces ,String type,long startTimeMillions,long endTimeMillions )throws Exception
    {
        List<WeekReport> weekReports = new ArrayList<WeekReport>();
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
                WeekReport wr = new WeekReport();
                String serviceId = (String) entry.getKey();
                wr.setServiceId(serviceId);
                //System.out.println("serviceId :--" + serviceId + " count:---" + entry.getDocCount());
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
                           // System.out.println("       inputCount :--" + inputCount.getValue() + " outputCount:---" + outputCount.getValue());
                        }
                    }
                }
                weekReports.add(wr);
            }
        }
        //关闭客户端
       // ClientUtils.closeClient(client);
        return weekReports;
    }

}
