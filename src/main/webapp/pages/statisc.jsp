<%--
  Created by IntelliJ IDEA.
  User: pc
  Date: 2016/6/28
  Time: 17:41
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>仪表盘:(DashBoard)</title>
</head>
<body>
<h2 style="color: blue">图形化展示</h2>
<iframe src="http://172.21.64.40:5601/app/kibana#/visualize/edit/%E4%B8%8D%E5%90%8C%E7%94%A8%E6%88%B7%E8%AE%BF%E9%97%AE%E6%AF%94?embed=true&_g=(refreshInterval:(display:Off,pause:!f,value:0),time:(from:now%2Fy,mode:quick,to:now%2Fy))&_a=(filters:!(),linked:!f,query:(query_string:(analyze_wildcard:!t,query:'*')),uiState:(spy:(mode:(fill:!f,name:!n))),vis:(aggs:!((id:'1',params:(),schema:metric,type:count),(id:'2',params:(customLabel:%E7%94%A8%E6%88%B7,field:userid.raw,order:desc,orderBy:'1',size:5),schema:segment,type:terms)),listeners:(),params:(addLegend:!t,addTooltip:!t,isDonut:!f,shareYAxis:!t),title:%E4%B8%8D%E5%90%8C%E7%94%A8%E6%88%B7%E8%AE%BF%E9%97%AE%E6%AF%94,type:pie))" height="100%" width="100%"></iframe>
</body>
</html>
