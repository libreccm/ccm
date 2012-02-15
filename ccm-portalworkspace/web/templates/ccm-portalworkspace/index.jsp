<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
       xmlns:define="/WEB-INF/bebop-define.tld"
         xmlns:show="/WEB-INF/bebop-show.tld"
            version="1.2">

  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
  <jsp:directive.page import="com.arsdigita.kernel.permissions.PrivilegeDescriptor"/>
  <jsp:directive.page import="com.arsdigita.portalworkspace.Workspace"/>
  <jsp:directive.page import="com.arsdigita.portalworkspace.ui.AbstractWorkspaceComponent"/>
  <jsp:directive.page import="com.arsdigita.portalworkspace.ui.DefaultWorkspaceSelectionModel"/>
  <jsp:directive.page import="com.arsdigita.toolbox.ui.ApplicationAuthenticationListener"/>

  <jsp:scriptlet>
      DispatcherHelper.cacheDisable(response);
  </jsp:scriptlet>

  <define:page name="viewWorkspace" application="portal" 
              title="Workspace"            cache="true">
    <jsp:scriptlet>
         if (Workspace.getConfig().getCheckWorkspaceReadPermissions()) {
             viewWorkspace.addRequestListener(
                 new ApplicationAuthenticationListener(PrivilegeDescriptor.READ));
         }
    </jsp:scriptlet>

    <define:component name="view"
                   classname="com.arsdigita.portalworkspace.ui.WorkspaceViewer" />
    <jsp:scriptlet>
      ((AbstractWorkspaceComponent) view).setWorkspaceModel(
                                          new DefaultWorkspaceSelectionModel());
    </jsp:scriptlet>
  </define:page>

  <show:all/>

</jsp:root>
