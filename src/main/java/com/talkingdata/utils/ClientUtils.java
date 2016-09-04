package com.talkingdata.utils;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.plugin.deletebyquery.DeleteByQueryPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * Created by ysl on 2016/6/30.
 */
public class ClientUtils {

    private static final Logger logger = LoggerFactory.getLogger(ClientUtils.class);
    private static Client client =null;

    static {
        try {
                Settings settings = Settings.settingsBuilder()
                        .put("cluster.name", "DMK-LOG")
                        .put("client.transport.sniff", true)
                        .build();
                client = TransportClient.builder().settings(settings)
                        .addPlugin(DeleteByQueryPlugin.class)
                        .build()
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("172.22.9.3"), 9300))
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("172.22.9.4"), 9300));
            } catch (UnknownHostException e) {
                logger.info(" UnknownHostException caught while initialize client ",e);
        }
    }
    /**
     *  获得默认情况下的ElasticSearch的client
     * @return
     * @throws Exception
     */
    public static Client getDefaultElasticSearchClient()throws Exception
    {
        return client;
    }
    public static Client getOwnElasticSearchClient(String clusterName,String ipAddresss,int port)throws Exception
    {
        Settings settings = Settings.settingsBuilder()
                .put("cluster.name", clusterName)
                .put("client.transport.sniff", true)
                .build();
        Client client = TransportClient.builder().settings(settings)
                .build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ipAddresss), port));
        return client;
    }

    /**
     * 关闭客户端
     * @param client
     */
    public static void closeClient(Client client)
    {
        client.close();
    }
}
