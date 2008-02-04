<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:define="/WEB-INF/bebop-define.tld" 
          xmlns:show="/WEB-INF/bebop-show.tld"
          version="1.2">

  <define:page name="SiteProxyItemPage" application="content" 
    title="CMS" cache="true">

    <define:component name="itemXML"
      classname="com.arsdigita.cms.dispatcher.ContentPanel"/>
    <define:component name="siteProxyItemXML"
      classname="com.arsdigita.cms.dispatcher.SiteProxyPanel"/>
  </define:page>

  <show:all/>
</jsp:root>
