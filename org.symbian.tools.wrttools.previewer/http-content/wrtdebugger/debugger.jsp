<%@ page import="org.symbian.tools.wrttools.previewer.http.WebAppInterface" %>
<%
	String widget = WebAppInterface.decode(request.getParameter("widget"));
	String id = request.getParameter("session");
%>
<html>
<head>
	<title><%=widget %></title>
	<% WebAppInterface.connectDebugger(widget, id); %>
	<script type="text/javascript">
		var req;
		
		function connect() {
			req = new XMLHttpRequest();
			req.onreadystatechange = testconnection;
			req.open("GET", "<%=WebAppInterface.getAjaxUri(widget, id) %>", true);
			req.send(null);
		}
		
		function testconnection() {
			if (req.readyState == 4) {
				if (req.status == 200) {
					window.setTimeout(connect, 200);
				} else {
					window.location = '<%=WebAppInterface.getUrl(widget, id) %>';
				}
			}
		}
	</script>
</head>
<body onload="connect()">
Establishing debug connection...
</body>