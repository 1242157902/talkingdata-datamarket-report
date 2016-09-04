package com.talkingdata.controller;

import com.talkingdata.domain.MyServiceInfor;
import com.talkingdata.service.MeasureService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * User：    ysl
 * Date:   2016/7/7
 * Time:   15:57
 */
public class MeasureServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)  {
        MeasureService measureService = new MeasureService();
        try {
            List<MyServiceInfor> myServiceInforList =  measureService.getUsersServices();
           // System.out.println(" 数量为："+myServiceInforList.size());
            req.setAttribute("myServiceInforList",myServiceInforList);
            req.getRequestDispatcher("/pages/adminStatisc.jsp").forward(req,resp);
        } catch (Exception e) {
            System.out.println("程序发生异常！");
            e.printStackTrace();
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         this.doGet(req,resp);
    }
}
