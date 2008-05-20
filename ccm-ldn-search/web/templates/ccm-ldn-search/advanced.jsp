<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:define="/WEB-INF/bebop-define.tld" 
          xmlns:show="/WEB-INF/bebop-show.tld"
          version="1.2">

  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
  <jsp:directive.page import="com.arsdigita.london.navigation.Navigation"/>
  <jsp:directive.page import="com.arsdigita.toolbox.ui.ApplicationAuthenticationListener"/>

  <jsp:scriptlet>
    DispatcherHelper.cacheDisable(response);
  </jsp:scriptlet>

  <define:page name="advanced" application="search" 
    title="Search" cache="true">
    <define:component name="categoryPath"
      classname="com.arsdigita.london.navigation.ui.category.Path"/>
    <define:component name="categoryMenu"
      classname="com.arsdigita.london.navigation.ui.category.Menu"/>
    <define:component name="search" classname="com.arsdigita.london.search.ui.AdvancedSearchPane"/>
  </define:page>

  <show:all/>
</jsp:root>
