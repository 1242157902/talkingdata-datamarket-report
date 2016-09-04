package com.talkingdata.domain;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * Created By IntelliJ IDEA.
 * User: LittleXuan
 * Date: 2015/11/18.
 * Time: 17:05
 * Desc: Userʵ����
 */
@XmlRootElement
public class User implements Serializable{
  private String userId;                      //用户id
  private String userName;                    //用户名称
  private List<Appkey> appkeys;               //该用户所拥有的appkey有哪些
  private List<ServiceInfor> serviceInforList;      //该用户所使用的暴漏给外部服务有哪些？


  public List<Appkey> getAppkeys() {
    return appkeys;
  }

  public void setAppkeys(List<Appkey> appkeys) {
    this.appkeys = appkeys;
  }

  public List<ServiceInfor> getServiceInforList() {
    return serviceInforList;
  }

  public void setServiceInforList(List<ServiceInfor> serviceInforList) {
    this.serviceInforList = serviceInforList;
  }

  public String getUserId() {
    return userId;
  }

  public String getUserName() {
    return userName;
  }


  public void setUserName(String userName) {
    this.userName = userName;
  }


  public void setUserId(String userId) {
    this.userId = userId;
  }
}