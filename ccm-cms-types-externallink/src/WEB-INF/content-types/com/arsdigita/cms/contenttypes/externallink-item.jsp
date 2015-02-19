<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:define="/WEB-INF/bebop-define.tld"
          xmlns:show="/WEB-INF/bebop-show.tld"
          version="1.2">
  <jsp:directive.page import="com.arsdigita.camden.cms.contenttypes.EForm"/>
  <jsp:directive.page import="com.arsdigita.cms.CMS"/>
  <jsp:directive.page import="com.arsdigita.cms.CMSContext"/>
  <jsp:scriptlet>
    CMSContext ctx = CMS.getContext();
    if (ctx.hasContentItem()) {
      EForm eform = (EForm) ctx.getContentItem();
      String url = eform.getURL();
      if (url != null &amp;&amp; url.length() &gt; 0) {
        response.sendRedirect(url);
      }
    }
  </jsp:scriptlet>
</jsp:root>
