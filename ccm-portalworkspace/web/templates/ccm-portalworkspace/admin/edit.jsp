<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
       xmlns:define="/WEB-INF/bebop-define.tld"
         xmlns:show="/WEB-INF/bebop-show.tld"
            version="1.2">

    <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
    <jsp:directive.page import="com.arsdigita.toolbox.ui.ApplicationAuthenticationListener"/>
    <jsp:directive.page import="com.arsdigita.portalworkspace.ui.admin.PeoplePane"/>
    <jsp:directive.page import="com.arsdigita.bebop.parameters.StringParameter"/>

    <jsp:scriptlet>
      DispatcherHelper.cacheDisable(response);
    </jsp:scriptlet>

    <define:page name="adminPage"        application="portal" 
                 title="Workspace Admin"        cache="true">

      <jsp:scriptlet>
         adminPage.addRequestListener(new ApplicationAuthenticationListener());
      </jsp:scriptlet>

      <define:component name="admin" 
                     classname="com.arsdigita.portalworkspace.ui.admin.PeoplePane" />

      <jsp:scriptlet>
        StringParameter action = new StringParameter("action");
        adminPage.addGlobalStateParam(action);
        ((PeoplePane) admin).init(action);
      </jsp:scriptlet>

    </define:page>

    <show:all/>
