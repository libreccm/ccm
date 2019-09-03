<!--
   The ScientificCMS customizable projects item page (index page) provided by package
   ccm-sci-types-project.
   In this version you can modify the elements to show inside the jsp.
   Sites will probably use a customized project list page. 

   ##Title: ScientificCMS Customizable Project Item Page 
   ##Descr: ScientificCMS page listing projects customizable inside the jsp
   ##Path : /templates/ccm-navigation/navigation/sci-projects-customizable.jsp
-->
<jsp:root
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:define="/WEB-INF/bebop-define.tld"
    xmlns:show="/WEB-INF/bebop-show.tld"
    version="1.2">

    <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
    <jsp:directive.page import="com.arsdigita.navigation.Navigation"/>
    <jsp:directive.page import="com.arsdigita.navigation.Navigation"/>
    <jsp:directive.page import="com.arsdigita.navigation.cms.CMSDataCollectionDefinition"/>
    <jsp:directive.page import="com.arsdigita.navigation.cms.CMSDataCollectionRenderer"/>
    <jsp:directive.page import="com.arsdigita.navigation.ui.object.CategoryFilter"/>

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
    <define:component name="categoryNav"
                      classname="com.arsdigita.navigation.ui.category.Hierarchy" />
    <jsp:scriptlet>
        ((com.arsdigita.navigation.ui.category.Hierarchy) categoryNav).setShowItems(false);
    </jsp:scriptlet>
        <define:component name="itemList"
                          classname="com.arsdigita.navigation.ui.object.CustomizableObjectList"/>

        <jsp:scriptlet>
 	    ((com.arsdigita.navigation.ui.object.CustomizableObjectList) itemList).addTextFilter("title", "title");
 	    CategoryFilter rfFilter = ((com.arsdigita.navigation.ui.object.CustomizableObjectList) itemList).addCategoryFilter("researchfield", "Forschungsfelder");
            rfFilter.setMultiple(false);
            ((com.arsdigita.navigation.ui.object.CustomizableObjectList) itemList).setCustomName("projectList");
	    ((com.arsdigita.navigation.ui.object.ComplexObjectList) itemList).setDefinition(new CMSDataCollectionDefinition());
            ((com.arsdigita.navigation.ui.object.ComplexObjectList) itemList).setRenderer(new CMSDataCollectionRenderer());
            ((com.arsdigita.navigation.ui.object.ComplexObjectList) itemList).getDefinition().setObjectType("com.arsdigita.cms.contenttypes.SciProject");
            ((com.arsdigita.navigation.ui.object.ComplexObjectList) itemList).getRenderer().setPageSize(50);
            ((com.arsdigita.navigation.ui.object.ComplexObjectList) itemList).getRenderer().setSpecializeObjects(true);
            ((com.arsdigita.navigation.ui.object.ComplexObjectList) itemList).getDefinition().addOrder("projectBegin DESC");
            ((com.arsdigita.navigation.ui.object.ComplexObjectList) itemList).getDefinition().addOrder("projectEnd DESC");
            ((com.arsdigita.navigation.ui.object.ComplexObjectList) itemList).getDefinition().addOrder("title ASC");

        </jsp:scriptlet>

        <define:component name="assignedTerms"
                          classname="com.arsdigita.navigation.ui.CategoryIndexAssignedTerms"/>

    </define:page>

    <show:all/>

</jsp:root>
