<%@ page import="com.arsdigita.cms.*,com.arsdigita.cms.dispatcher.ContentSectionDispatcher,com.arsdigita.dispatcher.*,com.arsdigita.persistence.*" %>

<%
   ContentSection section = 
     ContentSectionDispatcher.getContentSection(request);
   if (section != null) {
%>
<!--Begin top-level folder listing for a content section -->

<table cellpadding=0 cellspacing=0 border=0>
<%
   RequestContext rc = DispatcherHelper.getRequestContext(request);
   String folderPath = rc.getRemainingURLPart();
   folderPath = folderPath.substring(0, folderPath.lastIndexOf('/') + 1);

   DataQuery topFolders = SessionManager.getSession().retrieveQuery(
     "com.arsdigita.cms.liveTopLevelFolders");
   topFolders.setParameter("section_id", section.getID());

   while (topFolders.next()) {

     String topFolderPath = topFolders.get("folderName") + "/";
     String folderGraphic = topFolderPath.equals(folderPath) ? 
       "open" : "closed";
%>

<tr><td><img src="<%= request.getContextPath() %>/templates/ccm-cms/content-section/assets/folder-<%=folderGraphic%>.gif" width=17 height=17></td><td colspan="2">&nbsp;<a href="<%=section.getURL()%><%=topFolderPath%>index.jsp"><%=topFolders.get("folderLabel")%></a></td></tr>

<% if (folderPath.startsWith((String) topFolders.get("folderName"))) { %>
<!-- Begin subfolder block -->

<%
   DataQuery subFolders = SessionManager.getSession().retrieveQuery(
     "com.arsdigita.cms.liveSubFolders");
   subFolders.setParameter("root_folder_id", topFolders.get("folderID"));

   while (subFolders.next()) {
     // Begin subfolder loop

     String subFolderPath = topFolderPath + subFolders.get("folderName") + "/";
     String subFolderGraphic = subFolderPath.equals(folderPath) ? 
       "selected" : "closed";
%>

<tr><td width=17 height=17 bgcolor="white"><img src="<%= request.getContextPath() %>/templates/ccm-cms/content-section/assets/blank.gif" width=17 height=17><td><img src="<%= request.getContextPath() %>/templates/ccm-cms/content-section/assets/folder-<%=subFolderGraphic%>.gif" width=17 height=17><td>&nbsp;<a href="<%=section.getURL()%><%=subFolderPath%>index.jsp"><%=subFolders.get("folderLabel")%></a></td></tr>

<% } %>
<!-- End subfolder loop -->

<% } %>
<!-- End subfolder block -->

<% } %>
<!-- End top-level folder loop -->

</table>

<% } %>
<!-- End folder listing for a content section -->
