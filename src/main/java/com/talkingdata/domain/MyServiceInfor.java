package com.talkingdata.domain;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * 界面上所要输出的各项统计信息
 */
@XmlRootElement
public class MyServiceInfor {

	private String userId;				//用户id
	private String userName;			//用户名称或者公司名称
	private String startTime;			//用户传进来的起始时间
	private String endTime;				//用户传进来的结束时间

	private	long inputNumber;			//输入记录条数
	private long matchNumber;			//匹配记录条数，暂时没有
	private long outputNumber;		//输出记录条数


	private long totalUserNumer;		//调用次数总计
	private long totalSccessUse;		//调用成功次数总计
	private	long totalInputNumber;		//输入记录条数总计
	private long totalMmatchNumber;		//匹配记录条数总计
	private long totalOutputNumber;		//输出记录条数总计

	List<ServiceInfor> serviceInforList;	//每个用户标识下，所拥有的服务有哪些？


	public List<ServiceInfor> getServiceInforList() {
		return serviceInforList;
	}

	public void setServiceInforList(List<ServiceInfor> serviceInforList) {
		this.serviceInforList = serviceInforList;
	}

	public long getTotalUserNumer() {
		return totalUserNumer;
	}
	public void setTotalUserNumer(long totalUserNumer) {
		this.totalUserNumer = totalUserNumer;
	}
	public long getTotalSccessUse() {
		return totalSccessUse;
	}
	public void setTotalSccessUse(long totalSccessUse) {
		this.totalSccessUse = totalSccessUse;
	}
	public long getTotalInputNumber() {
		return totalInputNumber;
	}
	public void setTotalInputNumber(long totalInputNumber) {
		this.totalInputNumber = totalInputNumber;
	}
	public long getTotalMmatchNumber() {
		return totalMmatchNumber;
	}
	public void setTotalMmatchNumber(long totalMmatchNumber) {
		this.totalMmatchNumber = totalMmatchNumber;
	}
	public long getTotalOutputNumber() {
		return totalOutputNumber;
	}
	public void setTotalOutputNumber(long totalOutputNumber) {
		this.totalOutputNumber = totalOutputNumber;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public long getInputNumber() {
		return inputNumber;
	}
	public void setInputNumber(long inputNumber) {
		this.inputNumber = inputNumber;
	}
	public long getMatchNumber() {
		return matchNumber;
	}
	public void setMatchNumber(long matchNumber) {
		this.matchNumber = matchNumber;
	}
	public long getOutputNumber() {
		return outputNumber;
	}
	public void setOutputNumber(long outputNumber) {
		this.outputNumber = outputNumber;
	}
}
