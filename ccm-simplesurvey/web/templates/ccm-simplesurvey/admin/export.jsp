<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:define="/WEB-INF/bebop-define.tld" 
          xmlns:show="/WEB-INF/bebop-show.tld"
          version="1.2">

  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
  <jsp:directive.page import="com.arsdigita.toolbox.ui.ApplicationAuthenticationListener"/>
  <jsp:directive.page import="com.arsdigita.simplesurvey.ui.SurveySelectionModel"/>

  <jsp:scriptlet>
    DispatcherHelper.cacheDisable(response);
  </jsp:scriptlet>

  <define:page name="ssPage" application="simplesurvey" 
    title="Simple Survey Result Export" cache="true">

    <jsp:scriptlet>
      ssPage.addRequestListener(new ApplicationAuthenticationListener());

      SurveySelectionModel survey = new SurveySelectionModel(new BigDecimalParameter("survey"));
      ssPage.add(new com.arsdigita.simplesurvey.ui.admin.CSVFilePanel(survey));
      ssPage.addGlobalStateParam(survey.getStateParameter());
    </jsp:scriptlet>
  </define:page>

  <show:all/>
</jsp:root>
