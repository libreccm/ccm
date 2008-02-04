<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:define="/WEB-INF/bebop-define.tld" 
          xmlns:show="/WEB-INF/bebop-show.tld"
          version="1.2">

  <jsp:directive.page import="com.arsdigita.bebop.parameters.BigDecimalParameter"/>
  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
  <jsp:directive.page import="com.arsdigita.london.subsite.ui.SiteSelectionModel"/>
  <jsp:directive.page import="com.arsdigita.london.subsite.ui.ControlCenterPanel"/>
  <jsp:directive.page import="com.arsdigita.toolbox.ui.ApplicationAuthenticationListener"/>

  <jsp:scriptlet>
    DispatcherHelper.cacheDisable(response);
  </jsp:scriptlet>

  <define:page name="subsitePage" application="subsite" 
    title="Subsite Admin" cache="true">

    <jsp:scriptlet>
        subsitePage.addRequestListener(new ApplicationAuthenticationListener());

        SiteSelectionModel site = new SiteSelectionModel(new BigDecimalParameter("site"));
        subsitePage.add(new ControlCenterPanel(site));
        subsitePage.addGlobalStateParam(site.getStateParameter());
    </jsp:scriptlet>
  </define:page>

  <show:all/>
</jsp:root>
