package com.talkingdata.controller;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.talkingdata.domain.Appkey;
import com.talkingdata.domain.Quota;
import com.talkingdata.service.QuotaService;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;
import java.io.IOException;


/**
 * User：    ysl
 * Date:   2016/7/21
 * Time:   16:48
 */
@Path("/quota")
public class QuotaController {

    private static final  Logger logger = LoggerFactory.getLogger(QuotaController.class);
    private static final QuotaService  quotaService = new QuotaService();
    private static final Client client = Client.create();
    /**
     *通过appkey和服务id。来查询服务的调用情况
     * @param appkey            :appkey
     * @param serviceId   ：服务id
     * @return
     */
         @GET
         @Path("/getQuotaUsed")
         @Produces("application/json;Charset=UTF-8")
         public Quota getUserQuotaUsed(@QueryParam("appkey") String appkey,@QueryParam("serviceId") String serviceId)
    {
        try{
            System.out.println("appky:"+appkey+" 服务id:"+serviceId);
            return  quotaService.getQuotaUsed(appkey,serviceId);
        }catch (Exception e)
        {
            System.out.println("出现异常！");
            e.printStackTrace();
        }
        return null;
    }

    /**
     *通过appkey和服务id。来查询服务的调用情况
     * @param appkey            :appkey
     * @param serviceId   ：服务id
     * @return
     */
    @GET
    @Path("/getQuotaCost")
    @Produces("application/json;Charset=UTF-8")
    public Quota getUserQuotaUsedNew(@QueryParam("appkey") String appkey,@QueryParam("serviceId") String serviceId)
    {
        try{
            System.out.println(" appky:"+appkey+"    服务id:"+serviceId);
            return  quotaService.getQuotaUsedNew(appkey, serviceId);
        }catch (Exception e)
        {
            logger.info("Exception caught while accessing  QuotaController.java   function : getUserQuotaUsedNew()",e);
        }
        return null;
    }
    @GET
    @Path("/getQuotaUseds")
    @Produces("text/plain")
    public void getUserQuotaUseds(@QueryParam("appkey") String appkey,
                                  @QueryParam("serviceId") String serviceId,
                                  @Context HttpServletRequest httpServletRequest,
                                  @Context HttpServletResponse httpServletResponse)
    {
        try{
            System.out.println("appky:" + appkey + " 服务id" + serviceId);
            Appkey ak = quotaService.getUserQuotaUsed(appkey, serviceId);
            httpServletRequest.setAttribute("appkey",ak);
            httpServletRequest.getRequestDispatcher("/pages/quotaUsed.jsp").forward(httpServletRequest, httpServletResponse);
        }catch (Exception e)
        {
            System.out.println("出现异常！");
            e.printStackTrace();
        }
    }


    @GET
    @Path("/getQuota")
    @Consumes("application/json;charset=UTF-8")
    @Produces("application/json;charset=UTF-8")
    public Quota doGet(@QueryParam("appkey") @DefaultValue("") String appKey,
                          @QueryParam("serviceId") @DefaultValue("") String serviceId)
    {
        long start = System.currentTimeMillis();
        //The URL looks like
        //http://172.17.1.48:9200/logstash-log*/log/_search
        //String url = "http://172.17.1.48:9200/logstash-log*/log/_search";
        String url = "http://172.22.9.3:9200/logstash-gatewaylog/gatewaylog/_search";
        String result = doRequest(url, appKey, serviceId) ;
        Quota quota = getUsage(result);
        long parse = System.currentTimeMillis();
        quota.setServiceId(serviceId);
        quota.setAppkey(appKey);
        return quota;
    }

    public String doRequest(String baseURL, String appKey,String serviceId)
    {
        String entity = "";
        //The URL looks like
        //http://172.16.17.32:8080/tdaa/author?token=3be9d0be-e70d-4c45-95f5-0f85cebfe9ef&resource=newserviceA
        WebResource res = client.resource(baseURL);
        String query = genQueryBody(appKey, serviceId);
        //System.out.println(" 参数："+query);
        ClientResponse response = res.entity(query).post(ClientResponse.class);
        if (response != null)
        {
            EntityTag eTag = response.getEntityTag();
            entity = response.getEntity(String.class);
            logger.info(entity);
            //System.out.println(" 结果：" + entity);
            response.close();
        }
        return entity;
    }
    public String genQueryBody(String appKey,String serviceId)
    {
        String query = queryString.replace("{appKey}", appKey).replace("{serviceId}", serviceId);
        return query;
    }

    public Quota getUsage(String queryResponse)
    {
        Quota quota = new Quota ();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(queryResponse);
            if(node != null)
            {
                JsonNode aggNode = node.get("aggregations");
                    JsonNode countNode = aggNode.get("COUNT(*)");
                    JsonNode valueNode = countNode.get("value");
                    quota.setSuccessRequest(valueNode.asInt());
                    JsonNode countNode1 = aggNode.get("SUM(outputCount)");
                    JsonNode valueNode1 = countNode1.get("value");
                    quota.setOutRecords(valueNode1.asInt());
            }
        } catch (IOException e) {
            logger.error("Exception caught while parsing elastic search response.",e);
        }
        return quota;
    }
    static String queryString = "{"+
            "\"from\": 0,\n"+
            "\"size\": 0,\n"+
            "\"query\": {\n"+
            "\"bool\": {\n"+
            "\"must\": {\n"+
            "\"bool\": {\n"+
            "\"must\": [\n"+
            "{\n"+
            "\"match\": {\n"+
            "\"appkey\": {\n"+
            "\"query\": \"{appKey}\",\n"+
            "\"type\": \"phrase\"\n"+
            "}\n"+
            "}\n"+
            "},\n"+
            "{\n"+
            "\"match\": {\n"+
            "\"serviceid\": {\n"+
            "\"query\": \"{serviceId}\",\n"+
            "\"type\": \"phrase\"\n"+
            "}\n"+
            "}\n"+
            "},\n"+
            "{\n"+
            "\"match\": {\n"+
            "\"statusinner\": {\n"+
            "\"query\": 200,\n"+
            "\"type\": \"phrase\"\n"+
            "}\n"+
            "}\n"+
            "}\n"+
            "]\n"+
            "}\n"+
            "}\n"+
            "}\n"+
            "},\n"+
            "\"_source\": {\n"+
            "\"includes\": [\n"+
            "\"COUNT\"\n"+
            "],\n"+
            "\"excludes\": []\n"+
            "},\n"+
            "\"aggregations\": {\n"+

            "\"COUNT(*)\": {\n"+
            "\"value_count\": {\n"+
            "\"field\": \"_index\"\n"+
            "}\n"+
            "},\n"+
            "\"SUM(outputCount)\": {\n"+
            "\"sum\": {\n"+
            "\"field\": \"outputCount\"\n"+
            "}\n"+
            "}\n"+
            "}\n"+
            "}";
}
