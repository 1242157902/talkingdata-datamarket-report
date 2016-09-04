package com.talkingdata.dao;

import com.talkingdata.domain.Shrio;
import com.talkingdata.domain.WeekReport;
import com.talkingdata.utils.ClientUtils;
import com.talkingdata.utils.DateUtils;
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
 * User：    ysl
 * Date:   2016/8/11
 * Time:   17:53
 */
public class ShrioDao {

    public static void main(String[] args)
    {
        ShrioDao shrioDao = new ShrioDao();
        long startTimeMillions = DateUtils.dateToMillions(DateUtils.strToDate("2016-07-07 00:00:00"));
        long endTimeMillions = DateUtils.dateToMillions(DateUtils.strToDate("2016-08-10 23:59:59"));
        try {
              shrioDao.getSuccessAccessingRateOfDay("logstash-shirolog", "shirolog",
                      startTimeMillions, endTimeMillions);
           /* for(WeekReport weekReport:weekReports)
            {
                System.out.println(" seviceId:"+weekReport.getServiceId()+
                        " 成功请求次数："+weekReport.getSuccessRequest());
            }*/
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
    public Shrio getSuccessAccessingRateOfDay(String indeces ,String type,long startTimeMillions,long endTimeMillions )throws Exception
    {
        Shrio shrio = new Shrio();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(
                        QueryBuilders.rangeQuery("@timestamp")
                                    .from(startTimeMillions)
                                    .to(endTimeMillions)
                );
        //获得客户端
        Client client = ClientUtils.getDefaultElasticSearchClient();
        SearchRequestBuilder sbuilder = client
                .prepareSearch(indeces)
                .setQuery(
                        boolQuery
                )
                .addAggregation(
                        AggregationBuilders
                                .terms("result")
                                .field("result.raw")        //用户标识索引
                );
        //System.out.println("查询参数：--"+sbuilder);
        SearchResponse sr = sbuilder.execute().actionGet();
        //System.out.println("输出结果：--"+sr);
        Terms results = sr.getAggregations().get("result");
        // For each entry
        if(results!=null)
        {
            long totalAccess = 0l;
            for (Terms.Bucket entry : results.getBuckets())
            {
                String result = (String) entry.getKey();
                long resultCount = entry.getDocCount();
                //System.out.println("  result :--" + result + " count:---" + resultCount);
                totalAccess +=resultCount;
                if(result.equals("true"))
                {
                    shrio.setSuccessRequest(resultCount);
                }
            }
            shrio.setAccessRequest(totalAccess);
        }
        return  shrio;
    }
}
