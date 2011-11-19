<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:define="/WEB-INF/bebop-define.tld"
          xmlns:show="/WEB-INF/bebop-show.tld"
          version="1.2">

    <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
    <jsp:directive.page import="com.arsdigita.bebop.parameters.BigDecimalParameter"/>
    <jsp:directive.page import="com.arsdigita.navigation.Navigation"/>
    <jsp:directive.page import="com.arsdigita.navigation.NavigationModel"/>
    <jsp:directive.page import="com.arsdigita.navigation.cms.CMSNavigationModel"/>
    <jsp:directive.page import="com.arsdigita.navigation.cms.CMSDataCollectionDefinition"/>
    <jsp:directive.page import="com.arsdigita.navigation.cms.CMSDataCollectionRenderer"/>
    <jsp:directive.page import="com.arsdigita.cms.dispatcher.SimpleXMLGenerator"/>


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
                          classname="com.arsdigita.navigation.ui.CustomizableGreetingItem"/>
        <jsp:scriptlet>
            ((com.arsdigita.navigation.ui.CustomizableGreetingItem) greetingItem).loadTraversalAdapter("com.arsdigita.cms.contenttypes.SciOrganization",
                             SimpleXMLGenerator.ADAPTER_CONTEXT);
        ((com.arsdigita.navigation.ui.CustomizableGreetingItem) greetingItem).getTraversalAdapter().addAttributeProperty("/object/organizationDescription");
        ((com.arsdigita.navigation.ui.CustomizableGreetingItem) greetingItem).getTraversalAdapter().clearAssociationProperties();
        ((com.arsdigita.navigation.ui.CustomizableGreetingItem) greetingItem).getTraversalAdapter().addAssociationProperty("/object/persons");
        ((com.arsdigita.navigation.ui.CustomizableGreetingItem) greetingItem).getTraversalAdapter().addAssociationProperty("/object/departments/persons");
        ((com.arsdigita.navigation.ui.CustomizableGreetingItem) greetingItem).setOrder("surname asc, givenname asc");

        ((com.arsdigita.navigation.ui.CustomizableGreetingItem) greetingItem).setPageSize(20);
        </jsp:scriptlet>

        <define:component name="categoryPath"
                          classname="com.arsdigita.navigation.ui.category.Path"/>

        <define:component name="categoryMenu"
                          classname="com.arsdigita.navigation.ui.category.Menu"/>

        <define:component name="assignedTerms"
                          classname="com.arsdigita.navigation.ui.CategoryIndexAssignedTerms"/>

    </define:page>

    <show:all/>


</jsp:root>