<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:define="/WEB-INF/bebop-define.tld"
          xmlns:show="/WEB-INF/bebop-show.tld"
          version="1.2">
    <jsp:directive.page import="com.arsdigita.cms.contenttypes.ExternalLink"/>
    <jsp:directive.page import="com.arsdigita.cms.CMS"/>
    <jsp:directive.page import="com.arsdigita.cms.CMSContext"/>
    <jsp:scriptlet>
    CMSContext ctx = CMS.getContext();
    if (ctx.hasContentItem()) {
      ExternalLink extLink = (ExternalLink) ctx.getContentItem();
      String url = extLink.getURL();
      if (url != null &amp;&amp; !(url.isEmpty())  
          &amp;&amp; !(extLink.getShowComment())) {
        response.sendRedirect(url);
      }
    }
    </jsp:scriptlet>

    <define:page name="itemPage" 
                 application="content" 
                 title="CMS" 
                 cache="true">

        <define:component name="itemXML"
                          classname="com.arsdigita.cms.dispatcher.ContentPanel"/>
    </define:page>

    <show:all/>
</jsp:root>
