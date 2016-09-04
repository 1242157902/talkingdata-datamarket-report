package com.talkingdata.controller;

import com.talkingdata.domain.MyServiceInfor;
import com.talkingdata.service.MeasureService;
import com.talkingdata.utils.StringUtils;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.List;

/**
 * 针对计量管理界面
 * User：    ysl
 * Date:   2016/7/6
 * Time:   15:58
 */
@Path("/measure")
public class MeasureController {

    static MeasureService measureService = new MeasureService();
    /**
     * 该方法是将List<MyServiceInfor>以json的形式返回给调用者
     * @return
     */
    @GET
    @Path("/getService")
    @Produces("application/json;Charset=UTF-8")
    public List<MyServiceInfor> getService()
    {

        try {
           return  measureService.getUsersServices();
        } catch (Exception e) {
            System.out.println("程序发生异常！");
            e.printStackTrace();
        }
        return  null;
    }

    /**
     * 这个是为讲List<MyServiceInfor>放在request中，然后跳转页面
     * @param httpServletRequest
     * @param httpServletResponse
     */
    @GET
    @Path("/getServices")
    @Produces("text/plain")
    public void getServices(@Context HttpServletRequest httpServletRequest,
                            @Context HttpServletResponse  httpServletResponse)
    {
        try {
            List<MyServiceInfor> myServiceInforList =  measureService.getUsersServices();
            System.out.println(" 数量为："+myServiceInforList.size());
            httpServletRequest.setAttribute("myServiceInforList",myServiceInforList);
            httpServletRequest.getRequestDispatcher("/pages/adminStatisc.jsp").forward(httpServletRequest,httpServletResponse);
        } catch (Exception e) {
            System.out.println("程序发生异常！");
            e.printStackTrace();
        }
    }


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
                System.out.println(userId);
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

}
