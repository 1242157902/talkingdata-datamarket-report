package com.talkingdata.utils;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHelper {

    private Settings setting;

    private Map<String, Client> clientMap = new ConcurrentHashMap<String, Client>();

    private Map<String, Integer> ips = new HashMap<String, Integer>(); // hostname port

    private String clusterName = "DMK-LOG";

    private ClientHelper() {
        init();
        //TO-DO 添加你需要的client到helper
    }

    public static final ClientHelper getInstance() {
        return ClientHolder.INSTANCE;
    }

    private static class ClientHolder {
        private static final ClientHelper INSTANCE = new ClientHelper();
    }

    /**
     * 初始化默认的client
     */
    public void init() {
        ips.put("172.21.64.40", 9300);
        setting =  Settings
                .settingsBuilder()
                .put("client.transport.sniff",true)
                .put("cluster.name","DMK-LOG").build();
        addClient(setting, getAllAddress(ips));
    }

    /**
     * 获得所有的地址端口
     *
     * @return
     */
    public List<InetSocketTransportAddress> getAllAddress(Map<String, Integer> ips) {
        List<InetSocketTransportAddress> addressList = new ArrayList<InetSocketTransportAddress>();
        for (String ip : ips.keySet()) {
            //addressList.add(new InetSocketTransportAddress(ip,ips.get(ip)));
        }
        return addressList;
    }

    public Client getClient() {
        return getClient(clusterName);
    }

    public Client getClient(String clusterName) {
        return clientMap.get(clusterName);
    }

    public void addClient(Settings setting, List<InetSocketTransportAddress> transportAddress) {
        Client client = TransportClient.builder().settings(setting)
                .build()
                .addTransportAddresses(transportAddress.toArray(new InetSocketTransportAddress[transportAddress.size()]));
        clientMap.put(setting.get("cluster.name"), client);
    }
}