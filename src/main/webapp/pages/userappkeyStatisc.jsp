<%--
  Created by IntelliJ IDEA.
  User: ysl
  Date: 2016/7/7
  Time: 14:30
  To change this template use File | Settings | File Templates.
--%>
<%--注意要加 isELIgnored="false"   --%>
<%@ page  isELIgnored="false" contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <meta charset="utf-8">
    <script type="text/javascript" src="<c:url value="/pages/jedate/jedate.js"/>"></script>

    <titles><h2>统计页面</h2></titles>

    <style>
        body{ padding:50px 0 0 50px;}
        .datainp{ width:200px;  border:1px #ccc solid;}
        th{
            border-color: #00A1CB;
            border: 1px solid
        }
        td{
            border-color: #00A1CB;
            border: 1px solid
        }
    </style>
</head>
<body>
   <form action="/api/myAccount/getUserAccountByParams" method="get">
         <label>用户名称：</label> <input name="userName" id="userName"  value="15236282835@163.com"   readonly/>
         <label>服务id:</label> <input name="serviceIdNorth" id="serviceIdNorth" value=""/>
        <label>起始时间：</label><input class="datainp"  type="text" name="startTime" id="startTime" />
        <label>截止时间：</label> <input  class="datainp"  type="text"   name="endTime" id="endTime" />
       <input type="submit" value="提交" />

       <script type="text/javascript">
           //jeDate.skin('gray');
           jeDate({
               dateCell:"#startTime",
               format:"YYYY-MM-DD hh:mm:ss",
               isinitVal:false,
               isTime:true, //isClear:false,
               minDate:"2010-09-19 00:00:00",
               okfun:function(val){alert(val)}
           })
           jeDate({
               dateCell:"#endTime",
               format:"YYYY-MM-DD hh:mm:ss",
               isinitVal:false,
               isTime:true, //isClear:false,
               minDate:"2010-09-19 00:00:00",
               okfun:function(val){alert(val)}
           })
       </script>
    </form>

   <c:if test="${!empty user}">
        <table border="1">
            <thead><th>appkey</th><th>服务名称</th><th>调用次数</th><th>成功次数</th><th>失败次数</th><th>输入记录数</th><th>输出记录数</th></thead>
            <c:forEach items="${user.appkeys}" var="appkey">
                    <c:forEach items="${appkey.serviceInforList}" var="serviceInfor">
                        　　<tr>　<td>  ${appkey.appkey}</td>　<td>${serviceInfor.serviceNorth}</td> 　 <td>${serviceInfor.accessNumber}</td> 　<td>${serviceInfor.successNumber}</td>　<td>${serviceInfor.failedNumber}</td>
                                                  <td> ${serviceInfor.inRecordsSum}</td>　<td>${serviceInfor.outRecordsSum}</td> </tr>
                    </c:forEach>
            </c:forEach>
        </table>
    </c:if>
</body>
</html>
