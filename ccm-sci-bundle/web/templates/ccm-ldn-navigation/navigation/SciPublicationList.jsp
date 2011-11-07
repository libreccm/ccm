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
    <jsp:directive.page import="com.arsdigita.london.navigation.ui.object.CustomizableObjectList"/>

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
                          classname="com.arsdigita.london.navigation.ui.GreetingItem"/>
        <define:component name="categoryPath"
                          classname="com.arsdigita.london.navigation.ui.category.Path"/>
        <define:component name="categoryMenu"
                          classname="com.arsdigita.london.navigation.ui.category.Menu"/>
        <define:component name="itemList"
                          classname="com.arsdigita.london.navigation.ui.object.CustomizableObjectList"/>

      <jsp:scriptlet>
      CustomizableObjectList objList = (CustomizableObjectList) itemList;
      objList.setDefinition(new CMSDataCollectionDefinition());
      objList.setRenderer(new CMSDataCollectionRenderer());
      objList.setCustomName("SciPublicationsList");
      objList.getDefinition().setObjectType("com.arsdigita.cms.contenttypes.Publication");
      objList.getDefinition().setDescendCategories(false);      
      objList.addTextFilter("title", "title");
      objList.addTextFilter("authors.surname", "author");
      objList.addSelectFilter("yearOfPublication", "yearOfPublication", true, true, true, true);
      objList.addSortField("title", "title asc");     
      objList.addSortField("yearAsc", "yearOfPublication asc");
      objList.addSortField("yearDesc", "yearOfPublication desc");
      objList.addSortField("authors", "authors.surname asc, authors.givenname asc");
      objList.getDefinition().addOrder(objList.getOrder(request.getParameter("sort")));

      objList.getRenderer().setPageSize(20);
      objList.getRenderer().setSpecializeObjects(true);

      </jsp:scriptlet>

      <define:component name="assignedTerms"	
                        classname="com.arsdigita.london.navigation.ui.CategoryIndexAssignedTerms"/>
		 
    </define:page> 
    <show:all/>

</jsp:root>