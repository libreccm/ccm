<jsp:root
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:define="/WEB-INF/bebop-define.tld"
    xmlns:show="/WEB-INF/bebop-show.tld"
    version="1.2">

    <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
    <jsp:directive.page import="com.arsdigita.portalworkspace.ui.AbstractWorkspaceComponent"/>
    <jsp:directive.page import="com.arsdigita.portalworkspace.ui.jsp.DefaultWorkspaceSelectionModel"/>

    <jsp:scriptlet>
      DispatcherHelper.cacheDisable(response);
    </jsp:scriptlet>

    <define:page name="editWorkspace" application="portal"
                 title="Workspace Customize" cache="true">
      <define:component name="edit"
                   classname="com.arsdigita.portalworkspace.ui.jsp.WorkspaceEditor" />
      <jsp:scriptlet>
      ((AbstractWorkspaceComponent)edit).setWorkspaceModel(
                                             new DefaultWorkspaceSelectionModel());
      </jsp:scriptlet>
    </define:page>

    <show:all/>
</jsp:root>
