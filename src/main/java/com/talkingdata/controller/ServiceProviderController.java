package com.talkingdata.controller;

import com.talkingdata.domain.ServiceProvider;
import com.talkingdata.service.ServiceProviderService;
import com.talkingdata.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

/**
 * 服务提供者的服务的调用情况
 * User：    ysl
 * Date:   2016/7/19
 * Time:   14:24
 */
@Path("/serviceProvider")
public class ServiceProviderController {

    static ServiceProviderService serviceProviderService = new ServiceProviderService();

    /**
     * 服务提供者的报表信息
     * 该方法是将List<MyServiceInfor>以json的形式返回给调用者
     *
     * @return
     */
    @GET
    @Path("/getServicesByParam")
    @Produces("application/json;Charset=UTF-8")
    public ServiceProvider getServiceBySouthInfor(@QueryParam("serviceIdSouth")String serviceIdSouth,
                                                     @QueryParam("startTime")String startTime,
                                                     @QueryParam("endTime")String endTime)
    {

        try{
            if(!StringUtils.isEmpty(serviceIdSouth))
            {
                System.out.println(" 服务id："+serviceIdSouth);
                return serviceProviderService.getServiceBySouthInfor(serviceIdSouth,startTime,endTime);
            }else{
                return null;
            }
        }catch (Exception e)
        {
            System.out.println("程序异常！");
            e.printStackTrace();
        }
        return  null;
    }

    @GET
    @Path("/getServicesByParams")
    @Produces("text/plain")
    public void getServices(@QueryParam("serviceIdSouth")String serviceIdSouth,
                                                  @QueryParam("startTime")String startTime,
                                                  @QueryParam("endTime")String endTime,
                                       @Context HttpServletRequest httpServletRequest,
                                       @Context HttpServletResponse httpServletResponse
                                       )
    {
        ServiceProvider serviceProvider = null;
        try{
            if(!StringUtils.isEmpty(serviceIdSouth))
            {
                System.out.println(" 服务id："+serviceIdSouth);
                serviceProvider =  serviceProviderService.getServiceBySouthInfor(serviceIdSouth,startTime,endTime);
            }else{
                serviceProvider = null;
            }
            httpServletRequest.setAttribute("serviceProvider",serviceProvider);
            httpServletRequest.getRequestDispatcher("/pages/providerStatisc.jsp").forward(httpServletRequest,httpServletResponse);
        }catch (Exception e)
        {
            System.out.println("程序异常！");
            e.printStackTrace();
        }
    }
}
