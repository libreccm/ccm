<!--
   The ScientificCMS default departments item page (index page) provided by package
   ccm-sci-types-department.
   Sites will probably use a customized departmenbt list page. 

   ##Title: ScientificCMS Departments Item Page 
   ##Descr: ScientificCMS page listing departments
   ##Path : /templates/ccm-navigation/navigation/sci-departments.jsp
-->
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
        // Menu for mobile responsive version
        <define:component name="categoryNav"
                          classname="com.arsdigita.navigation.ui.category.Hierarchy" />
        <jsp:scriptlet>
            ((com.arsdigita.navigation.ui.category.Hierarchy) categoryNav).setShowItems(false);
        </jsp:scriptlet>


        <define:component name="itemList"
                          classname="com.arsdigita.navigation.ui.object.SimpleObjectList"/>
        <jsp:scriptlet>

            defaultItemPage.setClassAttr("departmentsPage");

            ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).setDefinition(new CMSDataCollectionDefinition());
            ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).setRenderer(new CMSDataCollectionRenderer());
            ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getDefinition().setObjectType("com.arsdigita.cms.ContentPage");

            ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getDefinition().setDescendCategories(false);
            ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getDefinition().addOrder("parent.categories.link.sortKey");

            ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().setPageSize(30);
            ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute("objectType");
            ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute("title");
            ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute("departmentShortDescription");
            ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute("contacts.contact_type");
            // ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute("contacts");
            // ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute("persons");
            ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "imageAttachments.caption");
            ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "imageAttachments.image.id");
        </jsp:scriptlet>

        <define:component name="assignedTerms"
                          classname="com.arsdigita.navigation.ui.CategoryIndexAssignedTerms"/>

    </define:page>
  <show:all/>
</jsp:root>
