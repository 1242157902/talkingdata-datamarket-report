package com.talkingdata.dao;

import com.talkingdata.domain.ServiceInfor;
import com.talkingdata.utils.ClientUtils;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.deletebyquery.DeleteByQueryAction;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequest;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequestBuilder;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User：    ysl
 * Date:   2016/8/3
 * Time:   14:59
 */
public class DeleteData {

    public static void main(String[] args)
    {

     String str = "167b6b2f-029a-418f-bf02-acb8c4adc543";
        DeleteData dd = new DeleteData();
        try {
           // for(String str:strs )
           // {
                System.out.println("#######");
                dd.DeleteUsersServices(str);
                System.out.println("#######***");
           // }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void DeleteUsersServices(String value) throws Exception
    {
        Client client = ClientUtils.getDefaultElasticSearchClient();

        StringBuilder b = new StringBuilder("");
        b.append("{");
        b.append("\"query\": {");
        b.append(" \"term\": {");
        b.append("\"trackid.raw\": " + "\"9ea7a6e8-712e-49bb-ac9c-613233bba2ee\"");
        b.append("}");
        b.append("}");
        b.append("}");
       /* Map<String,Object> map = new HashMap<>();
        map.put("trackid.raw", "167b6b2f-029a-418f-bf02-acb8c4adc543");*/
        DeleteByQueryResponse rsp = new DeleteByQueryRequestBuilder(client, DeleteByQueryAction.INSTANCE)
            .setIndices("logstash-gatewaylog")
            .setTypes("gatewaylog")
            .setSource(b.toString())
            .execute()
            .actionGet();
        //client.prepareDeleteByQuery("productIndex").setQuery(query).execute().actionGet();
       // ClientUtils.closeClient(client);

    }
}
