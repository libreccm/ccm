<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:define="/WEB-INF/bebop-define.tld"
          xmlns:show="/WEB-INF/bebop-show.tld"
          version="1.2">

<!-- JSP template to use a portal page as index page in navigation  
     Currently the same as def-portal.jsp, may change in future to
     support special features of a site's start page (home page)      
 
    ##Title: A Portal Page usable as Index Page in Navigation
    ##Descr: Portal Page including navigation menu. Portlets must be added separately.
    ##Path : /templates/ccm-navigation/navigation/def-portal-welcome.jsp
-->

    <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
    <jsp:directive.page import="com.arsdigita.navigation.Navigation"/>
    <jsp:directive.page import="com.arsdigita.bebop.parameters.BigDecimalParameter"/>
    <jsp:directive.page import="com.arsdigita.portalworkspace.ui.CategoryPortalSelectionModel"/>

    <jsp:scriptlet>
    long age = Navigation.getConfig().getIndexPageCacheLifetime();
    if (age == 0) {
      DispatcherHelper.cacheDisable(response);
    } else {
      DispatcherHelper.cacheForWorld(response, (int)age);
    }
    </jsp:scriptlet>

    <define:page name="portalsPage" 
                 application="navigation"
                 title="Navigation" 
                 cache="true">

        <define:component name="categoryPath"
                          classname="com.arsdigita.navigation.ui.category.Path"/>
        <define:component name="categoryMenu"
                          classname="com.arsdigita.navigation.ui.category.Menu"/>
        <define:component name="portalWorkspace"
                          classname="com.arsdigita.portalworkspace.ui.WorkspaceViewer"/>
        <jsp:scriptlet>
        portalsPage.setClassAttr("portalPage");
      ((com.arsdigita.portalworkspace.ui.WorkspaceViewer) portalWorkspace).setWorkspaceModel(new CategoryPortalSelectionModel());
        </jsp:scriptlet>
    </define:page>

    <show:all/>
</jsp:root>