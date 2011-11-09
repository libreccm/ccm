<jsp:root 
  xmlns:jsp="http://java.sun.com/JSP/Page" 
  xmlns:define="/WEB-INF/bebop-define.tld"
  xmlns:show="/WEB-INF/bebop-show.tld"
  version="1.2">

  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
  <jsp:directive.page import="com.arsdigita.bebop.parameters.BigDecimalParameter"/>
  <jsp:directive.page import="com.arsdigita.london.navigation.Navigation"/>
  <jsp:directive.page import="com.arsdigita.london.navigation.ui.AbstractObjectList"/>
  <jsp:directive.page import="com.arsdigita.london.navigation.cms.CMSDataCollectionDefinition"/>
  <jsp:directive.page import="com.arsdigita.london.navigation.cms.CMSDataCollectionRenderer"/>

  <jsp:scriptlet>
    long age = Navigation.getConfig().getIndexPageCacheLifetime();
    if (age == 0) {
      DispatcherHelper.cacheDisable(response);
    } else {
      DispatcherHelper.cacheForWorld(response, (int)age);
    }
  </jsp:scriptlet>

  <define:page name="demoPage" application="navigation"
    title="Navigation" cache="true">

    <define:component name="greetingItem"
      classname="com.arsdigita.london.navigation.ui.GreetingItem"/>
    <define:component name="categoryPath"
      classname="com.arsdigita.london.navigation.ui.category.Path"/>
    <define:component name="categoryMenu"
      classname="com.arsdigita.london.navigation.ui.category.Menu"/>
    <define:component name="categoryRoot"
      classname="com.arsdigita.london.navigation.ui.category.Root"/>
    <define:component name="categoryHierarchy"
      classname="com.arsdigita.london.navigation.ui.category.Hierarchy"/>
    <define:component name="categoryTopLevel"
      classname="com.arsdigita.london.navigation.ui.category.TopLevel"/>
    <define:component name="categoryChildren"
      classname="com.arsdigita.london.navigation.ui.category.Children"/>
    <define:component name="categorySiblings"
      classname="com.arsdigita.london.navigation.ui.category.Siblings"/>
    <define:component name="quickLinks"
      classname="com.arsdigita.london.navigation.ui.QuickLinks"/>
    <define:component name="itemList"
      classname="com.arsdigita.london.navigation.ui.object.AtoZObjectList"/>
    <jsp:scriptlet>
      AbstractObjectList obList = (AbstractObjectList)itemList;
      
      obList.setDefinition(new CMSDataCollectionDefinition());
      obList.setRenderer(new CMSDataCollectionRenderer());
      
      obList.getDefinition().setObjectType( "com.arsdigita.cms.ContentPage" );
      obList.getDefinition().setDescendCategories(true);
      
      obList.getRenderer().setPageSize(5);
      obList.getRenderer().addAttribute( "title" );
    </jsp:scriptlet>
  </define:page>

  <show:all/>
</jsp:root>