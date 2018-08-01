<jsp:root
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:define="/WEB-INF/bebop-define.tld"
    xmlns:show="/WEB-INF/bebop-show.tld"
    version="1.2">

<!-- JSP template to create a list of items with a complete set of elements
     (nav:attribute) instead the usual selection of elements (e.g. title and
     abstract/summary).
     It is primarily use for (scientific) publications and proects.
 
    ##Title: A special index page of content items with a complete list of attributes
    ##Descr: A special index page of content items with a complete list of attributes
    ##Path : /templates/ccm-navigation/navigation/def-specializing-list.jsp
-->

    <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
    <jsp:directive.page import="com.arsdigita.bebop.parameters.BigDecimalParameter"/>
    <jsp:directive.page import="com.arsdigita.navigation.Navigation"/>
    <jsp:directive.page import="com.arsdigita.navigation.cms.CMSDataCollectionDefinition"/>
    <jsp:directive.page import="com.arsdigita.navigation.cms.CMSDataCollectionRenderer"/>
    <jsp:directive.page import="org.apache.log4j.Logger"/>

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
        <define:component name="categoryPath"
                          classname="com.arsdigita.navigation.ui.category.Path"/>
        <define:component name="categoryMenu"
                          classname="com.arsdigita.navigation.ui.category.Menu"/>
        // Navigation menu for mobile devices (responsive)
        <define:component name="categoryNav"
                          classname="com.arsdigita.navigation.ui.category.Hierarchy">
            <jsp:scriptlet>
                ((com.arsdigita.navigation.ui.category.Hierarchy) categoryNav).setShowItems(false);
            </jsp:scriptlet>
        </define:component>

        <define:component name="itemList"
                          classname="com.arsdigita.navigation.ui.object.ComplexObjectList"/>
        <jsp:scriptlet>
            ((com.arsdigita.navigation.ui.object.ComplexObjectList) itemList).setDefinition(new CMSDataCollectionDefinition());
            ((com.arsdigita.navigation.ui.object.ComplexObjectList) itemList).setRenderer(new CMSDataCollectionRenderer());
            ((com.arsdigita.navigation.ui.object.ComplexObjectList) itemList).getDefinition().setObjectType("com.arsdigita.cms.ContentPage");
            ((com.arsdigita.navigation.ui.object.ComplexObjectList) itemList).getDefinition().addOrder("parent.categories.link.sortKey");
            ((com.arsdigita.navigation.ui.object.ComplexObjectList) itemList).getRenderer().setPageSize(20);
            ((com.arsdigita.navigation.ui.object.ComplexObjectList) itemList).getRenderer().setSpecializeObjects(true);
        </jsp:scriptlet>

        <define:component name="assignedTerms"
                          classname="com.arsdigita.navigation.ui.CategoryIndexAssignedTerms"/>
    </define:page>
    <show:all/>	
</jsp:root>