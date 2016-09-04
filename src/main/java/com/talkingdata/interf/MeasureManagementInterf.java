package com.talkingdata.interf;

import com.talkingdata.domain.MyServiceInfor;
import com.talkingdata.domain.ServiceInfor;

import java.util.List;

/**
 * 针对计量管理界面，根据界面要显示的数据来提供的接口
 * User：    ysl
 * Date:   2016/7/5
 * Time:   16:06
 */
public interface MeasureManagementInterf {

    /**
     *
     * @return :List<MyServiceInfor>
     * @param startTime	：起始时间，如果为空，则是从服务开始算起
     * @param endTime	：截止时间：如果唯恐，则是默认到现在为止
     * @param currentPage	：当前页
     * @param pageSize		：每页显示记录条数
     * @return
     *<p>Description:
     *				针对我的数据界面
     *				1、显示出用户，在该段时间内的所调用所的所有服务（分页显示）
     *				2、显示出每种服务的相关信息，包含服务名称、调用次数、成功次数、输入记录、匹配记录、输出记录等信息
     *	<p>
     *@date:     2016年6月30日下午12:03:56
     *@author:	  ysl
     */
    public List<MyServiceInfor> getAllService(String startTime,String endTime,int currentPage,int pageSize);

    /**
     *
     * @return :List<MyServiceInfor>
     * @param userId	:用户标识    ，代表用户或者公司名称
     * @param startTime	：起始时间：如果为空，则默认从提供服务时算起
     * @param endTime	：截止时间    ：如果为空，则默认到现在为止
     * @param currentPage	：当前页
     * @param pageSize		：每页显示记录条数
     * @return
     *<p>Description:
     *				针对我的数据界面
     *				1、显示出用户，在该段时间内的所调用所的所有服务（分页显示）
     *				2、显示出每种服务的相关信息，包含服务名称、调用次数、成功次数、输入记录、匹配记录、输出记录等信息
     *	<p>
     *@date:     2016年6月30日下午12:03:56
     *@author:	  ysl
     */
    public List<MyServiceInfor> getAllService(String userId,String startTime,String endTime,int currentPage,int pageSize);

    /**
     *
     * @return :List<ServiceInfor>
     * @param userId            :用户id
     * @param serviceId         ：服务id
     * @param startTime         ：起始时间，如果为空，则默认从提供服务时算起
     * @param endTime           ：截止时间，如果为空，则默认到现在
     * @param currentPage       ：当前页
     * @param pageSize          ：每页显示记录数
     * @return
     *<p>Description:		针对每种服务的调用情况：
     *					1、提供用户id，服务id
     *					2、返回该用户、该服务，在该段时间内的所有调用记录，分页显示
     *<p>
     *@date:     2016年6月30日下午12:08:18
     *@author:	  ysl
     */
    public List<ServiceInfor> getServiceInformation(String userId,String serviceId,String startTime,String endTime,int currentPage,int pageSize);
}
