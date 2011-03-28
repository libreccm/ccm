<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
	  xmlns:define="/WEB-INF/bebop-define.tld"
	  xmlns:show="/WEB-INF/bebop-show.tld"
	  version="1.2">

    <jsp:directive.page
        import="com.arsdigita.dispatcher.DispatcherHelper,
		        com.arsdigita.aplaws.ui.*" />

    <jsp:scriptlet>
        DispatcherHelper.cacheForWorld(response,900);
    </jsp:scriptlet>

<define:page name="itemPage" application="portal" title="APLAWS" cache="true">

  <define:component name="left" classname="com.arsdigita.aplaws.ui.HomepageWorkspace" />
  <define:component name="middle" classname="com.arsdigita.aplaws.ui.HomepageWorkspace" />
  <define:component name="right" classname="com.arsdigita.aplaws.ui.HomepageWorkspace" />

  <jsp:scriptlet>
HomepageWorkspaceSelectionModel workspace = new HomepageWorkspaceSelectionModel();
((HomepageWorkspace)left).setModel(new HomepagePortalSelectionModel(workspace, 0));
((HomepageWorkspace)left).setReadOnly(true);
((HomepageWorkspace)left).setName("left");
((HomepageWorkspace)left).addWidgets();
((HomepageWorkspace)middle).setModel(new HomepagePortalSelectionModel(workspace, 1));
((HomepageWorkspace)middle).setReadOnly(true);
((HomepageWorkspace)middle).setName("middle");
((HomepageWorkspace)middle).addWidgets();
((HomepageWorkspace)right).setModel(new HomepagePortalSelectionModel(workspace, 2));
((HomepageWorkspace)right).setReadOnly(true);
((HomepageWorkspace)right).setName("right");
((HomepageWorkspace)right).addWidgets();
  </jsp:scriptlet>
</define:page>

<show:all/>

</jsp:root>
