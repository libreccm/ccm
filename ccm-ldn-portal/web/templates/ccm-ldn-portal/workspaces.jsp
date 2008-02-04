<jsp:root
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:define="/WEB-INF/bebop-define.tld"
    xmlns:show="/WEB-INF/bebop-show.tld"
    version="1.2">

    <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>

    <jsp:scriptlet>
      DispatcherHelper.cacheDisable(response);
    </jsp:scriptlet>

    <define:page name="itemPage" application="portal" title="Workspace Directory" cache="true">
      <define:component name="left" classname="com.arsdigita.london.portal.ui.WorkspaceDirectoryComponent" />
    </define:page>

    <show:all/>
</jsp:root>
