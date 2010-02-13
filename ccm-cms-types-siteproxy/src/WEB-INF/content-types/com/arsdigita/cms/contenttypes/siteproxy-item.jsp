<%-- 
    Document   : siteproxy-item2
    Created on : 15.11.2009, 17:38:26
    Author     : pb
--%>

<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:define="/WEB-INF/bebop-define.tld" 
          xmlns:show="/WEB-INF/bebop-show.tld"
          version="1.2">

  <jsp:directive.page import="com.arsdigita.london.navigation.Navigation"/>

  <define:page name="SiteProxyItemPage" application="content" 
    title="CMS" cache="true">

    <define:component name="categoryPath"
      classname="com.arsdigita.london.navigation.ui.category.Path"/>
    <define:component name="categoryMenu"
      classname="com.arsdigita.london.navigation.ui.category.Menu"/>

    <define:component name="itemXML"
      classname="com.arsdigita.cms.dispatcher.ContentPanel"/>
    <define:component name="siteProxyItemXML"
      classname="com.arsdigita.cms.dispatcher.SiteProxyPanel"/>
  </define:page>

  <show:all/>
</jsp:root>
