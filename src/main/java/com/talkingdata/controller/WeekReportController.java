package com.talkingdata.controller;

import com.talkingdata.domain.WeekReport;
import com.talkingdata.service.WeekReportService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import java.util.List;

/**
 * 服务统计的提供接口
 * User：    ysl
 * Date:   2016/7/29
 * Time:   11:03
 */
@Path("/report")
public class WeekReportController {

    private static final WeekReportService weekReportService = new WeekReportService();
    private static final Logger logger = LoggerFactory.getLogger(WeekReportController.class);

    @GET
    @Path("/getDayReport")
    @Produces("application/json;Charset=UTF-8")
    public List<WeekReport> getWeekReport(@QueryParam("startTime") @DefaultValue("")String startTime,
                                    @QueryParam("endTime")@DefaultValue("")String endTime)
    {
        List<WeekReport> weekReports= null;
        logger.info("startTime:"+startTime+"  endTime:"+endTime);
        try {
            weekReports = weekReportService.getWeekServicesUsedInfor(startTime,endTime);
            logger.info("the returned result: "+weekReports.toString());
            return weekReports;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception caught while accessing function: getWeekReport",e);
            return weekReports;
        }
    }
}
