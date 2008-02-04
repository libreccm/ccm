<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:define="/WEB-INF/bebop-define.tld" 
          xmlns:show="/WEB-INF/bebop-show.tld"
          version="1.2">

  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
  <jsp:directive.page import="com.arsdigita.toolbox.ui.ApplicationAuthenticationListener"/>

  <jsp:scriptlet>
    DispatcherHelper.cacheDisable(response);
  </jsp:scriptlet>

  <define:page name="termsPage" application="terms" 
    title="Terms Admin" cache="true">

    <jsp:scriptlet>
        termsPage.addRequestListener(new ApplicationAuthenticationListener());
    </jsp:scriptlet>

    <define:component name="domains" classname="com.arsdigita.london.terms.ui.admin.DomainPanel"/>
  </define:page>

  <show:all/>
</jsp:root>
