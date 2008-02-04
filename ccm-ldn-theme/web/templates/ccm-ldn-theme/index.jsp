<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:define="/WEB-INF/bebop-define.tld" 
          xmlns:show="/WEB-INF/bebop-show.tld"
          version="1.2">

  <jsp:directive.page import="com.arsdigita.bebop.parameters.BigDecimalParameter"/>
  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
  <jsp:directive.page import="com.arsdigita.london.theme.ui.ThemeControlPanel"/>
  <jsp:directive.page import="com.arsdigita.toolbox.ui.ApplicationAuthenticationListener"/>

  <jsp:scriptlet>
    DispatcherHelper.cacheDisable(response);
  </jsp:scriptlet>

  <define:page name="themePage" application="theme" 
    title="Theme Admin" cache="true">

    <jsp:scriptlet>
      themePage.addRequestListener(new ApplicationAuthenticationListener());
      themePage.add(new ThemeControlPanel());
    </jsp:scriptlet>

  </define:page>

  <show:all/>
</jsp:root>
