<%@ page import="org.symbian.tools.wrttools.debug.internal.web.WebAppInterface, javax.servlet.http.HttpServletResponse" %>
<%
	String widget = WebAppInterface.decode(request.getParameter("widget"));
	String id = request.getParameter("session");
	
	if (WebAppInterface.isConnected(widget, id)) {
		response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
	}
%>