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
                          classname="com.arsdigita.london.navigation.ui.GreetingItem"/>
        <define:component name="categoryPath"
                          classname="com.arsdigita.london.navigation.ui.category.Path"/>
        <define:component name="categoryMenu"
                          classname="com.arsdigita.london.navigation.ui.category.Menu"/>
        <define:component name="itemList"
                          classname="com.arsdigita.london.navigation.ui.object.ComplexObjectList"/>
        <jsp:scriptlet>
            ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) itemList).setDefinition(new CMSDataCollectionDefinition());
            ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) itemList).setRenderer(new CMSDataCollectionRenderer());
            ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) itemList).getDefinition().setObjectType("com.arsdigita.cms.ContentPage");
            ((com.arsdigita.london.navigation.ui.object.SimpleObjectList) itemList).getDefinition().addOrder("parent.categories.link.sortKey");
            ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) itemList).getRenderer().setPageSize(20);
            ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) itemList).getRenderer().setSpecializeObjects(true);
        </jsp:scriptlet>

        <define:component name="assignedTerms"
                          classname="com.arsdigita.london.navigation.ui.CategoryIndexAssignedTerms"/>
        </jsp:scriptlet>
        <show:all/>
    </define:page>
</jsp:root>