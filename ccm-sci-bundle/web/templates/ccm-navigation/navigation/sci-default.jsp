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

    <define:page name="defaultItemPage" application="navigation"
                 title="Navigation" cache="true">

        <define:component name="greetingItem"
                          classname="com.arsdigita.navigation.ui.GreetingItem"/>
        <define:component name="categoryPath"
                          classname="com.arsdigita.navigation.ui.category.Path"/>
        <define:component name="categoryMenu"
                          classname="com.arsdigita.navigation.ui.category.Menu"/>
        <define:component name="itemList"
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
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "definition");
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "summary");
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "lead");
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "description");
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
