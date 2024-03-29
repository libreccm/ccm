<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:define="/WEB-INF/bebop-define.tld" 
          xmlns:show="/WEB-INF/bebop-show.tld"
	  version="1.2">

  <jsp:directive.page
    import="com.arsdigita.navigation.NavigationModel,
            com.arsdigita.navigation.cms.CMSNavigationModel"/>

  <jsp:declaration>
    NavigationModel model = new CMSNavigationModel();
  </jsp:declaration>

  <define:page name="itemPage" application="content-section" 
    title="APLAWS" cache="true">

    <define:component name="categoryPath"
      classname="com.arsdigita.navigation.ui.category.Path"/>
    <jsp:scriptlet>
       ((com.arsdigita.navigation.ui.category.Path)categoryPath)
         .setModel(model);
    </jsp:scriptlet>
    <define:component name="categoryMenu"
      classname="com.arsdigita.navigation.ui.category.Menu"/>
    <jsp:scriptlet>
       ((com.arsdigita.navigation.ui.category.Menu)categoryMenu)
         .setModel(model);
    </jsp:scriptlet>
    <define:component name="itemXML"
      classname="com.arsdigita.cms.dispatcher.ContentPanel"/>

    <define:component name="relatedItems"
      classname="com.arsdigita.navigation.ui.RelatedItems"/>

    <define:component name="assignedTerms"
      classname="com.arsdigita.bundle.ui.AssignedItemTerms"/>
  </define:page>

  <show:all/>

</jsp:root>
