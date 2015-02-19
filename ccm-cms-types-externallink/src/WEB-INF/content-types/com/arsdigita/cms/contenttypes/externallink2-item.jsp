<%@page import="com.arsdigita.camden.cms.contenttypes.EForm,com.arsdigita.cms.CMS,com.arsdigita.cms.CMSContext" %>
<%
    CMSContext ctx = CMS.getContext();
    if (ctx.hasContentItem()) {
      EForm eform = (EForm) ctx.getContentItem();
      String url = eform.getURL();
      String title = eform.getTitle();
      if (url != null && url.length() > 0) {
%>
<html>
<head>
<meta http-equiv="refresh" content="1;url=<%= url %>"/>
</head>
<body>
  <a href="<%= url %>"><%= title %></a>
</body>
</html>
<%
      }
    }
%>
