<%@ page contentType="text/html" %>
<%@ page import="com.arsdigita.db.*" %>
<%@ page import="java.sql.*" %>

<%

java.sql.Connection conn = ConnectionManager.getConnection();

    java.sql.PreparedStatement stmt =
        conn.prepareStatement(
            "select nt_requests.request_id, primary_email, messages.subject, messages.body, status " +
            "from nt_requests, messages, parties " +
            "where nt_requests.message_id = messages.message_id " +
            "and nt_requests.party_to = parties.party_id");

%>

<table border="1">
<tr>
<td align="center" colspan="5">Begin entries.</td>
</tr>
<tr>
<th>requestId</th>
<th>primaryEmail</th>
<th>subject</th>
<th>body</th>
<th>status</th>
<tr>

<%

    java.sql.ResultSet rs = stmt.executeQuery();
while ( rs.next() ) {
    String requestId = rs.getString(1);
    String primaryEmail = rs.getString(2);
    String subject = rs.getString(3);
    String body = rs.getString(4);
    String status = rs.getString(5);
%>

<td><%= requestId %></td>
<td><%= primaryEmail %></td>
<td><%= subject %></td>
<td><%= body %></td>
<td><%= status %></td>
</tr>
<tr>

<%
     }

rs.close();
stmt.close();
conn.close();

%>

<td align="center" colspan="5">End entries.</td>

</tr>
</table>
