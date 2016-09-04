package com.talkingdata.controller;

import com.talkingdata.domain.*;
import com.talkingdata.service.MeasureService;
import com.talkingdata.service.MyAccountService;
import com.talkingdata.utils.StringUtils;
import com.talkingdata.view.GraphView;
import com.talkingdata.view.MyAccountView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.List;

/**
 * 针对我的数据界面
 * User：    ysl
 * Date:   2016/7/14
 * Time:   10:55
 */
@Path("/myAccount")
public class MyAccountController {
   private  static final MyAccountService myAccountService = new MyAccountService();
    private static final  MeasureService measureService = new MeasureService();
    private static final Logger logger = LoggerFactory.getLogger(MyAccountController.class);

    /**
     *以条件形式来查询服务的调用情况，参数如下：
     *  并以json的形式返回给前台
     * @param userId            :用户id
     * @param serviceId         :服务id
     * @param startTime         ：起始时间
     * @param endTime           ：结束时间
     * @return
     */
    @GET
    @Path("/getServicesByParam")
    @Produces("application/json;Charset=UTF-8")
    public List<MyServiceInfor> getServicesByParam(@QueryParam("userId") String userId,@QueryParam("serviceId") String serviceId,
                                                   @QueryParam("startTime") String startTime,@QueryParam("endTime") String endTime)
    {

        System.out.println("  用户id"+userId+"  服务id"+serviceId+"  起始时间为："+startTime+
                "   结束时间："+endTime+"  结束时间："+endTime);
        try {
            if(!StringUtils.isEmpty(userId)&&
                    !StringUtils.isEmpty(serviceId)&&
                    !StringUtils.isEmpty(startTime)
                    )
            {
                return  measureService.statisticByUserIdAndTimeAndServiceId(userId, serviceId, startTime, endTime);
            }else if(!StringUtils.isEmpty(userId)&&
                    !StringUtils.isEmpty(serviceId)&&
                    StringUtils.isEmpty(startTime)
                    )
            {
                return measureService.statisticByUserIdAndServiceId(userId, serviceId);
            }
            else if(!StringUtils.isEmpty(userId)&&
                    StringUtils.isEmpty(serviceId)&&
                    !StringUtils.isEmpty(startTime)
                    )
            {
                return  measureService.statisticByUserIdAndTime(userId, startTime, endTime);
            }else if(!StringUtils.isEmpty(userId)&&
                    StringUtils.isEmpty(serviceId)&&
                    StringUtils.isEmpty(startTime)
                    )
            {
                return  measureService.statisticByUserId(userId);
            }else if(StringUtils.isEmpty(userId)&&
                    StringUtils.isEmpty(serviceId)&&
                    !StringUtils.isEmpty(startTime))
            {
                return measureService.getTimeServices(startTime,endTime);
            }
            else{
                return measureService.getUsersServices();
            }
        } catch (Exception e) {
            System.out.println("程序发生异常！");
            e.printStackTrace();
        }
        return  null;
    }

    /**
     *以条件形式查询服务的调用情况，参数如下：
     *  并将查询的结果放到request中，跳转到某个页面
     * @param userId            :用户id
     * @param serviceId         :服务id
     * @param startTime         ：起始时间
     * @param endTime            ：结束时间
     * @param httpServletRequest
     * @param httpServletResponse
     */
    @GET
    @Path("/getServicesByParams")
    @Produces("text/plain")
    public void getServicesByParams(@QueryParam("userId") String userId,@QueryParam("serviceId") String serviceId,
                                    @QueryParam("startTime") String startTime,@QueryParam("endTime") String endTime,
                                    @Context HttpServletRequest httpServletRequest,
                                    @Context HttpServletResponse  httpServletResponse)
    {
        List<MyServiceInfor> myServiceInforList = null;
        System.out.println("  用户id" + userId + "  服务id" + serviceId + "  起始时间为：" + startTime +
                "   结束时间：" + endTime + "  结束时间：" + endTime);
        try {
            if(!StringUtils.isEmpty(userId)&&
                    !StringUtils.isEmpty(serviceId)&&
                    !StringUtils.isEmpty(startTime)
                    )
            {
                myServiceInforList = measureService.statisticByUserIdAndTimeAndServiceId(userId, serviceId, startTime, endTime);
            }else if(!StringUtils.isEmpty(userId)&&
                    !StringUtils.isEmpty(serviceId)&&
                    StringUtils.isEmpty(startTime)
                    )
            {
                myServiceInforList =  measureService.statisticByUserIdAndServiceId(userId,serviceId);
            }
            else if(!StringUtils.isEmpty(userId)&&
                    StringUtils.isEmpty(serviceId)&&
                    !StringUtils.isEmpty(startTime)
                    )
            {
                myServiceInforList = measureService.statisticByUserIdAndTime(userId, startTime, endTime);
            }else if(!StringUtils.isEmpty(userId)&&
                    StringUtils.isEmpty(serviceId)&&
                    StringUtils.isEmpty(startTime)
                    )
            {
                myServiceInforList = measureService.statisticByUserId(userId);
            }else if(StringUtils.isEmpty(userId)&&
                    StringUtils.isEmpty(serviceId)&&
                    !StringUtils.isEmpty(startTime)
                    )
            {
                myServiceInforList = measureService.getTimeServices(startTime,endTime);
            }
            else{
                myServiceInforList =  measureService.getUsersServices();
            }
            httpServletRequest.setAttribute("myServiceInforList",myServiceInforList);
            httpServletRequest.getRequestDispatcher("/pages/adminStatisc.jsp").forward(httpServletRequest,httpServletResponse);
        } catch (Exception e) {
            System.out.println("程序发生异常！");
            e.printStackTrace();
        }

    }

    /**
     *根据用户名称、起始时间、结束时间来
     *  查询该用户的服务调用情况
     * @param userName          :用户名称
     * @param startTime         ：起始时间
     * @param endTime           ：结束时间
     * @return
     */
    @GET
    @Path("/getUserAccountByParam")
    @Produces("application/json;Charset=UTF-8")
    public User getUserAccount(@QueryParam("userName") String userName,@QueryParam("serviceIdNorth") String serviceIdNorth,
                               @QueryParam("startTime") String startTime,@QueryParam("endTime") String endTime)
    {

        try{
            return myAccountService.getUserServices(userName,serviceIdNorth,startTime,endTime);
        }catch (Exception e)
        {
            System.out.println("出现异常！");
            e.printStackTrace();
        }
        return null;
    }


    /**
     *通过用户名、起始时间、结束时间
     *      来查询服务的调用情况，而是将实体放入到request中去
     * @param userName              :用户名称
     * @param startTime             ：起始时间
     * @param endTime                ：结束时间
     * @param httpServletRequest        ：请求
     * @param httpServletResponse       ：回复
     */
    @GET
    @Path("/getUserAccountByParams")
    @Produces("text/plain")
    public void  getUserAccounts(@QueryParam("userName") String userName,
                                 @QueryParam("serviceIdNorth") String serviceIdNorth,
                               @QueryParam("startTime") String startTime,
                                 @QueryParam("endTime") String endTime,
                                @Context HttpServletRequest httpServletRequest,
                                @Context HttpServletResponse  httpServletResponse)
    {
        User user = null;
        try{
            System.out.print("服务id"+serviceIdNorth);
            user = myAccountService.getUserServices(userName,serviceIdNorth,startTime,endTime);
            httpServletRequest.setAttribute("user",user);
            httpServletRequest.getRequestDispatcher("/pages/userappkeyStatisc.jsp").forward(httpServletRequest,httpServletResponse);
        }catch (Exception e)
        {
            System.out.println("出现异常！");
            e.printStackTrace();
        }
    }

    /**
     *通过用户id，服务id，起始时间，结束时间来查询用户的服务的访问情况
     * @param userId                    :用户id       必填
     * @param serviceId                 ：服务id     可选
     * @param startTime                 ：起始时间      可选
     * @param endTime                   ：结束时间       可选
     * @return
     */
    @GET
    @Path("/getServicesByUserId")
    @Produces("application/json;Charset=UTF-8")
    public MyAccountView getsServicesByParams(@QueryParam("userId") @DefaultValue("")String userId,
                                                 @QueryParam("serviceId") @DefaultValue("")String serviceId,
                                                 @QueryParam("startTime") @DefaultValue("")String startTime,
                                                 @QueryParam("endTime")@DefaultValue("")String endTime)
    {
        MyAccountView myAccountView = new MyAccountView();
        List<WeekReport> weekReports= null;
        logger.info("userId:"+userId+"     serviceId:"+serviceId+"      startTime:"+startTime+"    endTime:"+endTime);
        if(!StringUtils.isEmpty(userId))
        {
            try {
                weekReports = myAccountService.getServicesByParams(userId,serviceId,startTime,endTime);
                myAccountView.setStatus(200);
                myAccountView.setMsg("ok");
                if(weekReports!=null)
                {
                    logger.info("the returned result: " + weekReports.toString());
                }
            } catch (Exception e) {
                logger.error("Exception caught while accessing  class : MyAccountController.class,     function: getsServicesByParams", e);
                myAccountView.setStatus(500);
                myAccountView.setMsg("error");
            }
        }else{
            myAccountView.setStatus(400);
            myAccountView.setMsg("userId not empty");
        }
        myAccountView.setDatas(weekReports);
        return myAccountView;
    }

    /**
     * 提供图表得数据
     * @param userId                    ：用户id
     * @param serviceId                 ：服务id
     * @param beforeDays                ：最近几天
     * @return
     */
    @GET
    @Path("/getGraphData")
    @Produces("application/json;Charset=UTF-8")
    public GraphView getGraphData(@QueryParam("userId") @DefaultValue("")String userId,
                             @QueryParam("serviceId") @DefaultValue("")String serviceId,
                             @QueryParam("beforeDays") @DefaultValue("7")int beforeDays)
    {
        GraphView graphView = new GraphView();
        List<DayReport> dayReports = null;
        if(!StringUtils.isEmpty(userId)&&!StringUtils.isEmpty(serviceId))
        {
            logger.info("parameters:   userId:"+userId+" serviceId:"+serviceId+" beforeDays:"+beforeDays);
            try {
                dayReports = myAccountService.getDataOfSomeTime(userId,serviceId,beforeDays);
                if(dayReports!=null)
                {
                    logger.info("the return result is :"+dayReports);
                }
                graphView.setStatus(200);
                graphView.setMsg("ok");
                graphView.setData(dayReports);
            } catch (Exception e) {
                 logger.info("Exception caught while accessing MyAccountController.java function:getGraphData()",e);
                graphView.setStatus(500);
                graphView.setMsg("error");
            }
        } else {
            graphView.setStatus(400);
            graphView.setMsg("userId or serviceId  is empty");
            logger.info("parmeters is error");
        }
        return  graphView;
    }
}
