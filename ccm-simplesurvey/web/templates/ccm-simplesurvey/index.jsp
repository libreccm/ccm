<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:define="/WEB-INF/bebop-define.tld" 
          xmlns:show="/WEB-INF/bebop-show.tld"
          version="1.2">

  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
  <jsp:directive.page import="com.arsdigita.bebop.parameters.BigDecimalParameter"/>
  <jsp:directive.page import="com.arsdigita.simplesurvey.ui.SurveySelectionModel"/>

  <jsp:scriptlet>
    DispatcherHelper.cacheDisable(response);
  </jsp:scriptlet>

  <define:page name="ssPage" application="simplesurvey" 
    title="Simple Survey" cache="true">

    <jsp:scriptlet>
      SurveySelectionModel survey = new SurveySelectionModel(new BigDecimalParameter("survey"));
      SurveySelectionModel poll = new SurveySelectionModel(new BigDecimalParameter("poll"));
      ssPage.add(new com.arsdigita.simplesurvey.ui.IndexPanel(survey, poll));
      ssPage.addGlobalStateParam(survey.getStateParameter());
      ssPage.addGlobalStateParam(poll.getStateParameter());
    </jsp:scriptlet>
  </define:page>

  <show:all/>
</jsp:root>
