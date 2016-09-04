package com.talkingdata.controller;

import com.talkingdata.domain.Shrio;
import com.talkingdata.domain.WeekReport;
import com.talkingdata.service.ShrioService;
import com.talkingdata.view.ShrioView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import java.util.List;

/**
 * User：    ysl
 * Date:   2016/8/12
 * Time:   13:38
 */
@Path("/shrio")
public class ShrioController {

    private static final ShrioService shrioService = new ShrioService();
    private static final Logger logger = LoggerFactory.getLogger(WeekReportController.class);

    /***
     *查询一段时间内，鉴权日志的访问次数，成功次数，默认情况下是返回前一天的数据
     * @param startTime
     * @param endTime
     * @return
     */
    @GET
    @Path("/getDayReport")
    @Produces("application/json;Charset=UTF-8")
    public ShrioView getWeekReport(@QueryParam("startTime") @DefaultValue("")String startTime,
                                          @QueryParam("endTime")@DefaultValue("")String endTime)
    {
        ShrioView shrioView = new ShrioView();
        logger.info("startTime:"+startTime+"  endTime:"+endTime);
        try {
            Shrio shrio = shrioService.getWeekServicesUsedInfor(startTime,endTime);
            logger.info("the returned result: "+shrio.toString());
            shrioView.setStatus(200);
            shrioView.setMsg("ok");
            shrioView.setData(shrio);
        } catch (Exception e) {
            logger.error("Exception caught while accessing ShrioController.java  function: getWeekReport",e);
            shrioView.setStatus(500);
            shrioView.setMsg("error");
        }
        logger.info(" the terminal result is :"+shrioView.toString());
        return  shrioView;
    }
}
