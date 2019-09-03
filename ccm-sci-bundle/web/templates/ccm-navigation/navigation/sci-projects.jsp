<!--
   The ScientificCMS default projects item page (index page) provided by package
   ccm-sci-types-project.
   This version uses a java class to collect the item elements to show.
   There is another version (customizable) where you can modify the elements to
   show inside the jsp.
   Sites will probably use a customized project list page. 

   ##Title: ScientificCMS Project Item Page 
   ##Descr: ScientificCMS page listing projects
   ##Path : /templates/ccm-navigation/navigation/sci-projects.jsp
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
                          classname="com.arsdigita.cms.sciproject.navigation.SciProjectList"/>

        <define:component name="assignedTerms"
                          classname="com.arsdigita.navigation.ui.CategoryIndexAssignedTerms"/>

    </define:page>

    <show:all/>

</jsp:root>
