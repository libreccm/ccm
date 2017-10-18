<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
       xmlns:define="/WEB-INF/bebop-define.tld" 
         xmlns:show="/WEB-INF/bebop-show.tld"
            version="1.2"> 

<!--
   Template to generate a sitemap page, Page includes mavigation mene and
   breadcrump if available.

   Same as def-sitemap.jsp, for backwards compatibility. Will be removed in the
   next version.

   ##Title: Sitemap Page
   ##Descr: Generates an overview about all navigation menu items. Backwards compatibel. Don't use for new installations.
   ##Path : /templates/ccm-navigation/navigation/def-sitemap.jsp
-->

  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>

  <jsp:scriptlet>
    DispatcherHelper.cacheForWorld( response );
  </jsp:scriptlet>

  <define:page name="sitemapPage" application="navigation"
    title="APLAWS" cache="true">

    <jsp:scriptlet>
      sitemapPage.setClassAttr("sitemapPage");
    </jsp:scriptlet>

    <define:component name="categoryPath"
      classname="com.arsdigita.navigation.ui.category.Path"/>
    <define:component name="categoryMenu"
      classname="com.arsdigita.navigation.ui.category.Menu"/>
    <define:component name="categoryNav"
                      classname="com.arsdigita.navigation.ui.category.Hierarchy">
      <jsp:scriptlet>
        ((com.arsdigita.navigation.ui.category.Hierarchy) categoryNav).setShowItems(false);
      </jsp:scriptlet>
    </define:component>

  </define:page>

  <show:all/>

</jsp:root>
