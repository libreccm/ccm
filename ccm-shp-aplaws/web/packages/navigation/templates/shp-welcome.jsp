<jsp:root 
  xmlns:jsp="http://java.sun.com/JSP/Page" 
  xmlns:define="/WEB-INF/bebop-define.tld"
  xmlns:show="/WEB-INF/bebop-show.tld"
  version="1.2">

  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
  <jsp:directive.page import="com.arsdigita.bebop.parameters.BigDecimalParameter"/>
  <jsp:directive.page import="com.arsdigita.london.navigation.Navigation"/>
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

  <define:page name="defaultItemPage" application="navigation"
    title="Navigation" cache="true">

    <define:component name="greetingItem"
      classname="com.arsdigita.london.navigation.ui.GreetingItem"/>
    <define:component name="categoryPath"
      classname="com.arsdigita.london.navigation.ui.category.Path"/>
    <define:component name="categoryMenu"
      classname="com.arsdigita.london.navigation.ui.category.Menu"/>
    <define:component name="itemList"
      classname="com.arsdigita.london.navigation.ui.object.SimpleObjectList"/>
    <jsp:scriptlet>
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) itemList).setDefinition(new CMSDataCollectionDefinition());
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) itemList).setRenderer(new CMSDataCollectionRenderer());
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) itemList).getDefinition().setObjectType("com.arsdigita.cms.ContentPage");

      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) itemList).getDefinition().setDescendCategories(false);      
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) itemList).getDefinition().addOrder("parent.categories.link.sortKey");
      
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) itemList).getRenderer().setPageSize(30);
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute("objectType");
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute("title");
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute("eventDate");
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute("launchDate");
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute("startDate");
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute("endDate");
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute("newsDate");
    </jsp:scriptlet>
    <define:component name="eventList"
      classname="com.arsdigita.london.navigation.ui.object.SimpleObjectList"/>
    <jsp:scriptlet>
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) eventList).setDefinition(new CMSDataCollectionDefinition());
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) eventList).setRenderer(new CMSDataCollectionRenderer());
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) eventList).getDefinition().setObjectType("com.arsdigita.cms.contenttypes.Event");

      //((com.arsdigita.london.navigation.ui.object.SimpleObjectList) eventList).getDefinition().getDataCollection(((com.arsdigita.london.navigation.ui.object.SimpleObjectList) eventList).getModel()).setFilter("now() between launchDate and endDate");

      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) eventList).getDefinition().setDescendCategories(true);      
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) eventList).getDefinition().addOrder("startDate");
      
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) eventList).getRenderer().setPageSize(5);
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) eventList).getRenderer().addAttribute("objectType");
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) eventList).getRenderer().addAttribute("title");
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) eventList).getRenderer().addAttribute("lead");
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) eventList).getRenderer().addAttribute("eventDate");
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) eventList).getRenderer().addAttribute("launchDate");
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) eventList).getRenderer().addAttribute("startDate");
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) eventList).getRenderer().addAttribute("endDate");
    </jsp:scriptlet>
    <define:component name="newsList"
      classname="com.arsdigita.london.navigation.ui.object.SimpleObjectList"/>
    <jsp:scriptlet>
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) newsList).setDefinition(new CMSDataCollectionDefinition());
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) newsList).setRenderer(new CMSDataCollectionRenderer());
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) newsList).getDefinition().setObjectType("com.arsdigita.cms.contenttypes.NewsItem");

      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) newsList).getDefinition().setDescendCategories(true);
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) newsList).getDefinition().addOrder("newsDate desc");

      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) newsList).getRenderer().setPageSize(5);
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) newsList).getRenderer().addAttribute("objectType");
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) newsList).getRenderer().addAttribute("title");
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) newsList).getRenderer().addAttribute("lead");
      ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) newsList).getRenderer().addAttribute("newsDate");
    </jsp:scriptlet>

    <define:component name="assignedTerms"
         classname="com.arsdigita.london.navigation.ui.CategoryIndexAssignedTerms"/>

  </define:page>
  <show:all/>
</jsp:root>
