<jsp:root
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:define="/WEB-INF/bebop-define.tld"
    xmlns:show="/WEB-INF/bebop-show.tld"
    version="1.2">

    <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
    <jsp:directive.page import="com.arsdigita.navigation.Navigation"/>

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
        <define:component name="projectList"
                          classname="com.arsdigita.bundle.ui.GenericOrgaUnitTabComponent"/>            

        <jsp:scriptlet>
            ((com.arsdigita.bundle.ui.GenericOrgaUnitTabComponent)projectList).setPage(defaultItemPage);
            ((com.arsdigita.bundle.ui.GenericOrgaUnitTabComponent)projectList).setOrgaUnit("com.arsdigita.cms.contenttypes.SciInstitute-id-3001");            
            ((com.arsdigita.bundle.ui.GenericOrgaUnitTabComponent)projectList).setOrgaUnitTab(new com.arsdigita.cms.contenttypes.ui.SciInstituteProjectsTab());
        </jsp:scriptlet>

        <define:component name="assignedTerms"    
                          classname="com.arsdigita.navigation.ui.CategoryIndexAssignedTerms"/>

    </define:page>
    
    <show:all/>

</jsp:root>