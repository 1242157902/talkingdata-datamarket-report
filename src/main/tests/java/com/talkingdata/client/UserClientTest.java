package com.talkingdata.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import org.junit.Test;

import javax.ws.rs.core.MediaType;


/**
 * Created by pc on 2016/6/29.
 */
public class UserClientTest {
    private WebResource r = null;
    @Test
    public void insertUser(){
        r = Client.create().resource("http://localhost:8080/api/quota/getQuotaCost");
        Form form = new Form();
        form.add("userId", "002");
        form.add("userName", "杨双亮");
        form.add("userAge", 23);
        ClientResponse response = r.type(MediaType.APPLICATION_FORM_URLENCODED)
                .post(ClientResponse.class, form);
        System.out.println(response.getStatus());
    }
    @Test
       public void getUser(){
        r = Client.create().resource("http://localhost:8080/jersery/users/getUser");
        Form form = new Form();
        form.add("userId", "002");
        form.add("userName", "杨双亮");
        form.add("userAge", 23);
        ClientResponse response = r.type(MediaType.APPLICATION_FORM_URLENCODED)
                .post(ClientResponse.class, form);
        System.out.println(response.getStatus());
    }
}
