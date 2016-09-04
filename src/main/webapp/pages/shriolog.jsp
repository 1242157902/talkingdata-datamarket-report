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
    <title>鉴权日志</title>
</head>
<body>
<h2 style="color: blue">图形化展示</h2>
   <a href="http://172.21.64.40:5601/app/kibana#/discover?_g=(refreshInterval:(display:Off,pause:!f,value:0),time:(from:now%2Fw,mode:quick,to:now%2Fw))&_a=(columns:!(_source),index:logstash-shirolog,interval:auto,query:(query_string:(analyze_wildcard:!t,query:'*')),sort:!('@timestamp',desc),uiState:(spy:(mode:(fill:!f,name:!n))))">
     鉴权日志
   </a>
</body>
</html>
