<%@page import="com.arsdigita.cms.contenttypes.ExternalLink,
                com.arsdigita.cms.CMS,
                com.arsdigita.cms.CMSContext" %>
<%
    CMSContext ctx = CMS.getContext();
    if (ctx.hasContentItem()) {
      ExternalLink extLink = (ExternalLink) ctx.getContentItem();
      String url = extLink.getURL();
      String title = extLink.getTitle();
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
