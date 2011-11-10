<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
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

  <define:page name="defaultItemPage" application="navigation"
    title="Navigation" cache="false">

    <define:component name="greetingItem"
      classname="com.arsdigita.navigation.ui.GreetingItem"/>
    <define:component name="sectionXML"
      classname="com.arsdigita.cms.contenttypes.ui.SciDepartmentWithPublicationsPanel"/>
    <jsp:scriptlet>
        com.arsdigita.cms.ContentItem item = (com.arsdigita.cms.ContentItem)((com.arsdigita.london.navigation.ui.GreetingItem) greetingItem).getObject();
        ((com.arsdigita.cms.contenttypes.ui.SciDepartmentPanel) sectionXML).setContentItem(item);
    </jsp:scriptlet>
    <define:component name="categoryPath"
      classname="com.arsdigita.navigation.ui.category.Path"/>
    <define:component name="categoryMenu"
      classname="com.arsdigita.navigation.ui.category.Menu"/>
    <define:component name="itemList"
      classname="com.arsdigita.navigation.ui.object.SimpleObjectList"/>
    <jsp:scriptlet>
      ((com.arsdigita.cms.contenttypes.ui.SciDepartmentPanel)sectionXML).setPageSize(30);

      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).setDefinition(new CMSDataCollectionDefinition());
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).setRenderer(new CMSDataCollectionRenderer());
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getDefinition().setObjectType("com.arsdigita.cms.ContentPage");

      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getDefinition().setDescendCategories(false);      
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getDefinition().addOrder("parent.categories.link.sortKey");
      
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().setPageSize(30);
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute("objectType");
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute("title");
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "definition");
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "summary");
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "lead");
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "description");
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "eventDate");
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "startDate" );
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "endDate");
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "newsDate");
    </jsp:scriptlet>

    <define:component name="assignedTerms"
         classname="com.arsdigita.navigation.ui.CategoryIndexAssignedTerms"/>

  </define:page>
  <show:all/>
</jsp:root>

