package com.talkingdata.controller;





import com.talkingdata.domain.User;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;


import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2016/6/29.
 */

@Path("/users")
public class UserResource {
    @Context
    UriInfo uriInfo;
    /**
     * 增加用户
     * @param userId
     * @param userName
     * @param userAge
     * @param servletResponse
     * @throws IOException
     */
    @POST
    @Path("/addUser")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void addUser(
            @FormParam("userId") String userId,
            @FormParam("userName") String userName,
            @FormParam("userAge") int userAge,
            @Context HttpServletResponse servletResponse
    ) throws IOException {
       // User user = new User(userId,userName,userAge);
      //  UserCache.getUserCache().put(userId, user);
        System.out.println("userName:--" + userName + " userId:--" + userId + " userAge:--" + userAge);
        URI uri = uriInfo.getAbsolutePathBuilder().path(userId).build();
        Response.created(uri).build();
    }
    @GET
    @Path("/getUser/{userId}")
    @Produces("application/json;Charset=UTF-8")
    public User  getUser(@PathParam("userId") String userId) {
        System.out.println("输入的用户id为：--"+userId);
        User user = new User();
       // user.setUserAge(23);
        user.setUserId("3");
        user.setUserName("english");
        return  user;
    }
    @GET
    @Path("/getUserList/{userId}")
    @Produces("application/json;Charset=UTF-8")
    public  List<User>  getUserList(@PathParam("userId") String userId) {
        System.out.println("获取用户列表：输入的用户id为：--"+userId);
        List<User> userList = new ArrayList<User>();
        User user = new User();
       // user.setUserAge(23);
        user.setUserId("3");
        user.setUserName("english");
        User user1 = new User();
        user1.setUserId("4");
        user1.setUserName("ysl");
       // user1.setUserAge(45);
        userList.add(user);
        userList.add(user1);
        return  userList;
    }
    @GET
    @Path("/getUserName/{username}")
    @Produces("text/plain")
    public String getUserName(@PathParam("username") String userName)
    {
        return  userName;
    }

    @GET
    @Path("/getUserNameAndAge")
    @Produces("application/json")
    public User getUser(@QueryParam("name") String name,
                        @QueryParam("age") int age,
                        @QueryParam("id") String id
                        ) {
        User user = new User();
       // user.setUserAge(age);
        user.setUserId(id);
        user.setUserName(name);
        return  user;
    }
}
