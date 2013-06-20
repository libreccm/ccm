<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:define="/WEB-INF/bebop-define.tld" 
          xmlns:show="/WEB-INF/bebop-show.tld"
          version="1.2">

  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
  <jsp:directive.page import="com.arsdigita.navigation.Navigation"/>
  <jsp:directive.page import="com.arsdigita.toolbox.ui.ApplicationAuthenticationListener"/>

  <jsp:scriptlet>
    DispatcherHelper.cacheDisable(response);
  </jsp:scriptlet>

  <define:page name="remote" application="search" 
    title="Remote Search" cache="true">
    <define:component name="categoryPath"
      classname="com.arsdigita.navigation.ui.category.Path"/>
    <define:component name="categoryMenu"
      classname="com.arsdigita.navigation.ui.category.Menu"/>
    <jsp:scriptlet>
      ((com.arsdigita.navigation.ui.category.Path) categoryPath).setModel(new com.arsdigita.subsite.SearchNavigationModel());
      ((com.arsdigita.navigation.ui.category.Menu) categoryMenu).setModel(new com.arsdigita.subsite.SearchNavigationModel());
    </jsp:scriptlet>
    <define:component name="search" classname="com.arsdigita.london.search.ui.RemoteSearchPane"/>
  </define:page>

  <show:all/>
</jsp:root>
