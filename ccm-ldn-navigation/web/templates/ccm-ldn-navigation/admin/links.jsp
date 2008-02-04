<jsp:root
  xmlns:jsp="http://java.sun.com/JSP/Page"
  xmlns:define="/WEB-INF/bebop-define.tld"
  xmlns:show="/WEB-INF/bebop-show.tld"
  version="1.2">

  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
  <jsp:directive.page import="com.arsdigita.toolbox.ui.ApplicationAuthenticationListener"/>
  <jsp:directive.page import="com.arsdigita.kernel.permissions.PrivilegeDescriptor"/>
  <jsp:directive.page import="com.arsdigita.london.navigation.ui.admin.PopulatePathListener"/>

  <jsp:scriptlet>
    DispatcherHelper.cacheDisable(response);
  </jsp:scriptlet>

  <define:page name="adminPage" application="navigation"
    title="Navigation Links Admin" cache="true">

    <define:component name="quickLinks"
      classname="com.arsdigita.london.navigation.ui.admin.QuickLinkPanel"/>
    <define:component name="categoryPath"
      classname="com.arsdigita.london.navigation.ui.category.Path"/>
    <define:component name="categoryMenu"
      classname="com.arsdigita.london.navigation.ui.category.Menu"/>


    <jsp:scriptlet>
        adminPage.addRequestListener(new ApplicationAuthenticationListener(PrivilegeDescriptor.EDIT));
        adminPage.addRequestListener(new PopulatePathListener());
    </jsp:scriptlet>
  </define:page>

  <show:all/>
</jsp:root>
