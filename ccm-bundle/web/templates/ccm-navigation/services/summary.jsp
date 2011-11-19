<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:define="/WEB-INF/bebop-define.tld" 
          xmlns:show="/WEB-INF/bebop-show.tld"
	  version="1.2"> 

  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>

  <jsp:scriptlet>
    DispatcherHelper.cacheForWorld( response );
  </jsp:scriptlet>

  <define:page name="summaryPage" application="navigation"
    title="APLAWS" cache="true">

    <define:component name="categoryPath"
      classname="com.arsdigita.navigation.ui.category.Path"/>
    <define:component name="categoryMenu"
      classname="com.arsdigita.navigation.ui.category.Menu"/>
    <define:component name="itemSummary"
      classname="com.arsdigita.aplaws.ui.TermItemSummary"/>

  </define:page>

  <show:all/>

</jsp:root>
