<%@ page import="com.arsdigita.persistence.SessionManager, java.sql.*" %>
<%@ page import="com.arsdigita.dispatcher.*" %>

<%
response.addHeader("Expires", "-1");
response.addHeader("Cache-control", "no-cache");
Connection conn = SessionManager.getSession().getConnection();
PreparedStatement stmt = 
    conn.prepareStatement("insert into redirect_race_test (singleton, when) " 
    + "values (?, sysdate)");
stmt.setObject(1, new Integer(request.getParameter("random")));
stmt.executeUpdate();

DispatcherHelper.sendRedirect(request, response, "redirect-race-2.jsp");

// delay before committing transaction
%>

