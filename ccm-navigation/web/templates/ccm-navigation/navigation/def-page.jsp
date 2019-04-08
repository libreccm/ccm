<!--
   The default content item page (index page) provided by ccm-navigation package.
   Sites will probably use a customized index page. 
   Set the actual in dex page to be used by parameter
      com.arsdigita.london.navigation.default_template

   ##Title: Default Content Items Page 
   ##Descr: Navigation Index Page, ordering items in ascending order, manually adjustable
   ##Path : /templates/ccm-navigation/navigation/def-page.jsp
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

    <jsp:scriptlet>
      long age = Navigation.getConfig().getIndexPageCacheLifetime();
      if (age == 0) {
        DispatcherHelper.cacheDisable(response);
      } else {
        DispatcherHelper.cacheForWorld(response, (int)age);
      }
      int maxItems = Navigation.getConfig().getIndexPageMaxItems();
    </jsp:scriptlet>

    <define:page name="defaultItemPage" application="navigation"
        title="Navigation" cache="true">

        <define:component name="greetingItem"
                          classname="com.arsdigita.navigation.ui.GreetingItem"/>

        <define:component name="categoryPath"
                          classname="com.arsdigita.navigation.ui.category.Path"/>

        <define:component name="categoryMenu"
                          classname="com.arsdigita.navigation.ui.category.Menu"/>

        // Menu for mobile responsive version
        <define:component name="categoryNav"
                          classname="com.arsdigita.navigation.ui.category.Hierarchy">
            <jsp:scriptlet>
                ((com.arsdigita.navigation.ui.category.Hierarchy) categoryNav).setShowItems(false);
            </jsp:scriptlet>
        </define:component>

        <define:component name="itemList"
                          classname="com.arsdigita.navigation.ui.object.SimpleObjectList"/>

        <jsp:scriptlet>
          ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).setDefinition(new CMSDataCollectionDefinition());
          ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).setRenderer(new CMSDataCollectionRenderer());
          ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getDefinition().setObjectType("com.arsdigita.cms.ContentPage");

          ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getDefinition().setDescendCategories(false);      
          ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getDefinition().addOrder("parent.categories.link.sortKey");
      
          ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().setPageSize(maxItems);
          ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute("objectType");
          ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute("title");
          ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "definition");
          ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "summary");
          ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "lead");
          ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "description");
          ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "launchDate");
          ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "eventDate");
          ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "startDate" );
          ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "endDate");
          ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "newsDate");
          ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "imageAttachments.caption");
          ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "imageAttachments.image.id");
          ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "pageDescription");
        </jsp:scriptlet>

        <define:component name="quickLinks"
                          classname="com.arsdigita.navigation.ui.QuickLinks"/>

        <define:component name="assignedTerms"
                          classname="com.arsdigita.navigation.ui.CategoryIndexAssignedTerms"/>

    </define:page>
  <show:all/>
</jsp:root>
