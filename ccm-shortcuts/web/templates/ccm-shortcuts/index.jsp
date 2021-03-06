<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:define="/WEB-INF/bebop-define.tld" 
          xmlns:show="/WEB-INF/bebop-show.tld"
          version="1.2">

  <jsp:directive.page
       import="com.arsdigita.dispatcher.DispatcherHelper"/>
  <jsp:directive.page
       import="com.arsdigita.toolbox.ui.ApplicationAuthenticationListener"/>

  <jsp:scriptlet>
    DispatcherHelper.cacheDisable(response);
  </jsp:scriptlet>

  <define:page name="shortcutsPage" application="shortcuts" 
               title="Shortcuts Admin" cache="true">

    <define:component name="admin"
                      classname="com.arsdigita.shortcuts.ui.AdminPanel"/>

    <jsp:scriptlet>
      shortcutsPage.addRequestListener(new ApplicationAuthenticationListener());
    </jsp:scriptlet>
  </define:page>

  <show:all/>
</jsp:root>
