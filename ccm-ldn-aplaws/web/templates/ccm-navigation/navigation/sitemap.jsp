<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
       xmlns:define="/WEB-INF/bebop-define.tld" 
         xmlns:show="/WEB-INF/bebop-show.tld"
            version="1.2"> 

  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>

  <jsp:scriptlet>
    DispatcherHelper.cacheForWorld( response );
  </jsp:scriptlet>

  <define:page name="sitemapPage" application="navigation"
    title="APLAWS" cache="true">

    <define:component name="categoryNav"
      classname="com.arsdigita.navigation.ui.category.Hierarchy">
      <jsp:scriptlet>
        ((com.arsdigita.navigation.ui.category.Hierarchy) categoryNav).setShowItems(false);
      </jsp:scriptlet>
    </define:component>

  </define:page>

  <show:all/>

</jsp:root>
