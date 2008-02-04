<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:define="/WEB-INF/bebop-define.tld" 
          xmlns:show="/WEB-INF/bebop-show.tld"
          version="1.2">

  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
  <jsp:directive.page import="com.arsdigita.bebop.parameters.BigDecimalParameter"/>
  <jsp:directive.page import="com.arsdigita.london.atoz.ui.admin.AdminPane"/>
  <jsp:directive.page import="com.arsdigita.toolbox.ui.ApplicationAuthenticationListener"/>

  <jsp:scriptlet>
    DispatcherHelper.cacheDisable(response);
  </jsp:scriptlet>

  <define:page name="atozPage" application="atoz" 
    title="A-Z" cache="true">

    <jsp:scriptlet>
        atozPage.addRequestListener(new ApplicationAuthenticationListener());

        BigDecimalParameter provider = new BigDecimalParameter("provider");
        atozPage.add(new AdminPane(provider));
        atozPage.addGlobalStateParam(provider);
    </jsp:scriptlet>
  </define:page>

  <show:all/>
</jsp:root>
