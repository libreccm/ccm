<jsp:root
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:define="/WEB-INF/bebop-define.tld"
    xmlns:show="/WEB-INF/bebop-show.tld"
    version="1.2">

    <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
    <jsp:directive.page import="com.arsdigita.toolbox.ui.ApplicationAuthenticationListener"/>

    <jsp:scriptlet>
      DispatcherHelper.cacheDisable(response);
    </jsp:scriptlet>

    <define:page name="adminPage" application="portal" title="Workspace Admin" cache="true">
      <jsp:scriptlet>
         adminPage.addRequestListener(new ApplicationAuthenticationListener());
      </jsp:scriptlet>
      <define:component name="admin" classname="com.arsdigita.portalworkspace.ui.admin.AdminPane" />
    </define:page>

    <show:all/>
</jsp:root>
