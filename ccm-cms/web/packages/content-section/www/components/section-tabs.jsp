<%@ page import="com.arsdigita.dispatcher.*" %>
<%@ taglib uri="/WEB-INF/jsp-template.tld" prefix="acs" %>

<% 
  RequestContext context = DispatcherHelper.getRequestContext(request);
  String sectionPath = context.getProcessedURLPart();
  String tabClass = (sectionPath.equals("")) ? "navtopon" : "navtopoff";
%>

<td class="<%=tabClass%>">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="/" style="color:white;" class="nav">HOME</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>

<acs:query name="com.arsdigita.cms.allContentSections" />

<%
 while (dataQuery.next()) { 

   String sectionURL = (String) dataQuery.get("sectionURL");
   tabClass = (sectionPath.startsWith(sectionURL)) ? "navtopon" : "navtopoff";
%>

<td class="<%=tabClass%>">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="<%=sectionURL%>index.jsp" style="color:white;" class="nav"><%=dataQuery.get("sectionLabel")%></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>

<% } %>
