<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:define="/WEB-INF/bebop-define.tld"
          xmlns:show="/WEB-INF/bebop-show.tld"
          version="1.2">

    <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
    <jsp:directive.page import="com.arsdigita.bebop.parameters.BigDecimalParameter"/>
    <jsp:directive.page import="com.arsdigita.london.navigation.Navigation"/>
    <jsp:directive.page import="com.arsdigita.london.navigation.NavigationModel"/>
    <jsp:directive.page import="com.arsdigita.london.navigation.cms.CMSNavigationModel"/>
    <jsp:directive.page import="com.arsdigita.london.navigation.cms.CMSDataCollectionDefinition"/>
    <jsp:directive.page import="com.arsdigita.london.navigation.cms.CMSDataCollectionRenderer"/>
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
                          classname="com.arsdigita.london.navigation.ui.CustomizableGreetingItem"/>
        <jsp:scriptlet><![CDATA[
            ((com.arsdigita.london.navigation.ui.CustomizableGreetingItem) greetingItem).loadTraversalAdapter("com.arsdigita.cms.contenttypes.SciDepartment",
                             SimpleXMLGenerator.ADAPTER_CONTEXT);
        ((com.arsdigita.london.navigation.ui.CustomizableGreetingItem) greetingItem).getTraversalAdapter().addAttributeProperty("/object/departmentDescription");
        ((com.arsdigita.london.navigation.ui.CustomizableGreetingItem) greetingItem).getTraversalAdapter().clearAssociationProperties();
        ((com.arsdigita.london.navigation.ui.CustomizableGreetingItem) greetingItem).getTraversalAdapter().addAssociationProperty("/object/projects");

        java.util.Calendar today = new java.util.GregorianCalendar();
        String todayDate = String.format("%d-%2d-%2d", today.get(java.util.Calendar.YEAR),
                                                today.get(java.util.Calendar.MONTH) + 1,
                                                today.get(java.util.Calendar.DAY_OF_MONTH));
        ((com.arsdigita.london.navigation.ui.CustomizableGreetingItem) greetingItem).addFilter("projectend <= '" + todayDate + "'");

        ((com.arsdigita.london.navigation.ui.CustomizableGreetingItem) greetingItem).setPageSize(20);
        ]]></jsp:scriptlet>

        <define:component name="categoryPath"
                          classname="com.arsdigita.london.navigation.ui.category.Path"/>

        <define:component name="categoryMenu"
                          classname="com.arsdigita.london.navigation.ui.category.Menu"/>

        <define:component name="assignedTerms"
                          classname="com.arsdigita.london.navigation.ui.CategoryIndexAssignedTerms"/>

    </define:page>

    <show:all/>


</jsp:root>