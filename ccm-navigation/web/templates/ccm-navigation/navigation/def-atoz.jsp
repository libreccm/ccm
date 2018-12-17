<jsp:root 
  xmlns:jsp="http://java.sun.com/JSP/Page" 
  xmlns:define="/WEB-INF/bebop-define.tld"
  xmlns:show="/WEB-INF/bebop-show.tld"
  version="1.2">

  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
  <jsp:directive.page import="com.arsdigita.bebop.parameters.BigDecimalParameter"/>
  <jsp:directive.page import="com.arsdigita.navigation.Navigation"/>
  <jsp:directive.page import="com.arsdigita.navigation.cms.CMSDataCollectionDefinition"/>
  <jsp:directive.page import="com.arsdigita.navigation.cms.CMSDataCollectionRenderer"/>

  <jsp:scriptlet>
    long age = Navigation.getConfig().getIndexPageCacheLifetime();
    if (age == 0) {
      DispatcherHelper.cacheDisable(response);
    } else {
      DispatcherHelper.cacheForWorld(response, (int)age);
    }
  </jsp:scriptlet>

  <define:page name="atozItemPage" application="navigation"
    title="Navigation" cache="true">

    <define:component name="greetingItem"
      classname="com.arsdigita.navigation.ui.GreetingItem"/>
    <define:component name="categoryPath"
      classname="com.arsdigita.navigation.ui.category.Path"/>
    <define:component name="categoryMenu"
      classname="com.arsdigita.navigation.ui.category.Menu"/>

    // Used by responsive mobile navigation menu
    <define:component name="categoryNav"
                      classname="com.arsdigita.navigation.ui.category.Hierarchy">
    </define:component>
    <jsp:scriptlet>
                ((com.arsdigita.navigation.ui.category.Hierarchy) categoryNav).setShowItems(false);
    </jsp:scriptlet>

    <define:component name="itemList"
      classname="com.arsdigita.navigation.ui.object.AtoZObjectList"/>
    <jsp:scriptlet>
      ((com.arsdigita.navigation.ui.object.AtoZObjectList) itemList).setDefinition(new CMSDataCollectionDefinition());
      ((com.arsdigita.navigation.ui.object.AtoZObjectList) itemList).setRenderer(new CMSDataCollectionRenderer());
      ((com.arsdigita.navigation.ui.object.AtoZObjectList) itemList).getDefinition().setObjectType("com.arsdigita.cms.ContentPage");

      ((com.arsdigita.navigation.ui.object.AtoZObjectList) itemList).getDefinition().setDescendCategories(false);      
      ((com.arsdigita.navigation.ui.object.AtoZObjectList) itemList).getDefinition().addOrder("title");
      
      ((com.arsdigita.navigation.ui.object.AtoZObjectList) itemList).getRenderer().setPageSize(20);
      ((com.arsdigita.navigation.ui.object.AtoZObjectList) itemList).getRenderer().addAttribute("objectType");
      ((com.arsdigita.navigation.ui.object.AtoZObjectList) itemList).getRenderer().addAttribute( "title");
      ((com.arsdigita.navigation.ui.object.AtoZObjectList) itemList).getRenderer().addAttribute( "definition");
      ((com.arsdigita.navigation.ui.object.AtoZObjectList) itemList).getRenderer().addAttribute( "summary");
      ((com.arsdigita.navigation.ui.object.AtoZObjectList) itemList).getRenderer().addAttribute( "lead");
    </jsp:scriptlet>
  </define:page>

  <show:all/>
</jsp:root>
