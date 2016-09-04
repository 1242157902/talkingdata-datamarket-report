package com.talkingdata.controller;

import com.talkingdata.domain.ServiceInformation;
import com.talkingdata.domain.WeekReport;
import com.talkingdata.service.ServiceManagerService;
import com.talkingdata.view.ServiceinformationView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import java.util.List;

/**
 *对应服务管理界面，所有服务的列表，或者通过相应的服务id，查询服务的被调用情况
 * User：    ysl
 * Date:   2016/8/16
 * Time:   12:01
 */
@Path("/service")
public class ServiceManagerController {

    private static final ServiceManagerService serviceManagerService = new ServiceManagerService();
    private static final Logger logger = LoggerFactory.getLogger(ServiceManagerController.class);

    @GET
    @Path("/getServices")
    @Produces("application/json;Charset=UTF-8")
    public ServiceinformationView getWeekReport(@QueryParam("serviceId") @DefaultValue("")String serviceId,
                                          @QueryParam("userId")@DefaultValue("")String userId,
                                          @QueryParam("startTime") @DefaultValue("")String startTime,
                                          @QueryParam("endTime")@DefaultValue("")String endTime)
    {
        ServiceinformationView sv = new ServiceinformationView();
        List<ServiceInformation> serviceInformations= null;
        logger.info("serviceId:"+serviceId+"  userId:"+userId+"  startTime:"+startTime+"  endTime:"+endTime);
        try {
            sv.setMsg("ok");
            sv.setStatus(200);
            serviceInformations =  serviceManagerService.getServiceInformations(serviceId,userId,startTime,endTime);
            logger.info("the returned result: "+serviceInformations.toString());
            sv.setData(serviceInformations);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception caught while accessing function: getWeekReport",e);
            sv.setMsg("error");
            sv.setStatus(500);
        }
        return sv;
    }
}
