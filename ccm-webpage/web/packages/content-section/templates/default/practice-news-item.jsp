<jsp:root 
  xmlns:jsp="http://java.sun.com/JSP/Page" 
  xmlns:define="/WEB-INF/bebop-define.tld"
  xmlns:show="/WEB-INF/bebop-show.tld"
  version="1.2">

  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
  <jsp:directive.page import="com.arsdigita.bebop.parameters.BigDecimalParameter"/>
  <jsp:directive.page import="com.arsdigita.london.navigation.Navigation"/>

  <define:page name="itemPage" application="navigation"
    title="Navigation" cache="true">

    <define:component name="categoryPath"
      classname="com.arsdigita.london.navigation.ui.CategoryPath"/>
    <jsp:scriptlet>
      ((com.arsdigita.london.navigation.ui.CategoryPath) categoryPath).setSelectionType( com.arsdigita.london.navigation.ui.Selection.USE_ITEM_ID );
    </jsp:scriptlet>
    <define:component name="categoryNavRecursive"
      classname="com.arsdigita.london.navigation.ui.CategoryNavRecursive"/>
    <jsp:scriptlet>
      ((com.arsdigita.london.navigation.ui.CategoryNavRecursive) categoryNavRecursive).setSelectionType( com.arsdigita.london.navigation.ui.Selection.USE_ITEM_ID );
    </jsp:scriptlet>
    <define:component name="contentPanel"
      classname="com.arsdigita.cms.webpage.ui.ContentPanelWebpageNode"/>
    <define:component name="contentSectionComponent"
      classname="com.arsdigita.cms.ui.ContentSectionComponent"/>
  </define:page>

  <show:all/>
</jsp:root>
