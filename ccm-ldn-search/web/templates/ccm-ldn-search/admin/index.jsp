<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:define="/WEB-INF/bebop-define.tld" 
          xmlns:show="/WEB-INF/bebop-show.tld"
          version="1.2">

  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
  <jsp:directive.page import="com.arsdigita.toolbox.ui.ApplicationAuthenticationListener"/>

  <jsp:scriptlet>
    DispatcherHelper.cacheDisable(response);
  </jsp:scriptlet>

  <define:page name="admin" application="search" 
    title="Search Administration" cache="true">

    <jsp:scriptlet>
      admin.addRequestListener(new ApplicationAuthenticationListener());
    </jsp:scriptlet>

    <define:component name="search" classname="com.arsdigita.london.search.ui.admin.ServersPanel"/>
    <define:component name="links" classname="com.arsdigita.london.search.ui.admin.SponsoredLinksPanel"/>
  </define:page>

  <show:all/>
</jsp:root>
