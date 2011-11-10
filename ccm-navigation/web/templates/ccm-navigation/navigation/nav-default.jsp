<!--
   A default item page (index page) provided by ccm-navigation package. Sites
   will probably use a customized index page. 
   Set the actual in dex page to be used by parameter
      com.arsdigita.london.navigation.default_template

   ##Title: Default Items Page
   ##Descr: Default Navigation Index Page, ordering items in ascending order
   ##Path : /templates/ccm-navigation/navigation/nav-default.jsp
-->
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
  <jsp:directive.page import="com.arsdigita.navigation.DataCollectionDefinition"/>
  <jsp:directive.page import="com.arsdigita.navigation.DataCollectionRenderer"/>
  

  <jsp:scriptlet>
    long age = Navigation.getConfig().getIndexPageCacheLifetime();
    if (age == 0) {
      DispatcherHelper.cacheDisable(response);
    } else {
      DispatcherHelper.cacheForWorld(response, (int)age);
    }
  </jsp:scriptlet>

  <define:page name="defaultItemPage" application="navigation"
    title="Navigation" cache="true">

    <define:component name="greetingItem"
      classname="com.arsdigita.navigation.ui.GreetingItem"/>
    <define:component name="greetingItemExtraXML"
      classname="com.arsdigita.navigation.ui.GreetingItemExtraXML"/>
    <define:component name="categoryPath"
      classname="com.arsdigita.navigation.ui.category.Path"/>
    <define:component name="categoryMenu"
      classname="com.arsdigita.navigation.ui.category.Menu"/>
    <define:component name="itemList"
      classname="com.arsdigita.navigation.ui.object.SimpleObjectList"/>
    <define:component name="applicationList"
      classname="com.arsdigita.navigation.ui.object.SimpleObjectList"/>
   
    <jsp:scriptlet>
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).setDefinition(new CMSDataCollectionDefinition());
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).setRenderer(new CMSDataCollectionRenderer());
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getDefinition().setObjectType("com.arsdigita.cms.ContentPage");

      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getDefinition().setDescendCategories(false);      
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getDefinition().addOrder("parent.categories.link.sortKey");
      
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().setPageSize(30);
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute("objectType");
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute("title");
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute("displayName");
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute("launchDate");
      
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) applicationList).setDefinition(new DataCollectionDefinition());
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) applicationList).setRenderer(new DataCollectionRenderer());
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) applicationList).getDefinition().setObjectType("com.arsdigita.web.Application");

      ((com.arsdigita.navigation.ui.object.SimpleObjectList) applicationList).getDefinition().setDescendCategories(false);      
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) applicationList).getDefinition().addOrder("categories.link.sortKey");
      
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) applicationList).getRenderer().setPageSize(30);
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) applicationList).getRenderer().addAttribute("displayName");
      
    </jsp:scriptlet>

    <define:component name="assignedTerms"
         classname="com.arsdigita.navigation.ui.CategoryIndexAssignedTerms"/>
   <define:component name="dateOrderCategories"
      classname="com.arsdigita.navigation.ui.DateOrderedCategoryComponent"/>

  </define:page>
  <show:all/>
</jsp:root>
