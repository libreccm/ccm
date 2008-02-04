<%@ page import="com.arsdigita.persistence.SessionManager, java.sql.*" %>
<!--
for whatever reason, there is no way for me to tell for
sure whether this page is being cached or not.
one way to discourage caching is to beef up the file
size.
-->
<%
response.addHeader("Cache-control", "no-cache");
response.addHeader("Expires", "-1");
Connection conn = SessionManager.getSession().getConnection();
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery("select * from redirect_race_test "
    + " where when = (select max(when) from redirect_race_test)");
rs.next();
%>
<%= rs.getString("singleton") %>
