<jsp:root
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:define="/WEB-INF/bebop-define.tld"
    xmlns:show="/WEB-INF/bebop-show.tld"
    version="1.2">

    <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
    <jsp:directive.page import="com.arsdigita.bebop.parameters.BigDecimalParameter"/>
    <jsp:directive.page import="com.arsdigita.london.navigation.Navigation"/>
    <jsp:directive.page import="com.arsdigita.london.navigation.cms.CMSDataCollectionDefinition"/>
    <jsp:directive.page import="com.arsdigita.london.navigation.cms.CMSDataCollectionRenderer"/>

    <jsp:scriptlet>
    long age = Navigation.getConfig().getIndexPageCacheLifetime();
    if (age == 0) {
      DispatcherHelper.cacheDisable(response);
    } else {
      DispatcherHelper.cacheForWorld(response, (int)age);
    }
    </jsp:scriptlet>

    <define:page name="publications"
                 application="content"                 
                 title="Publications4Homepages"
                 cache="true">

        <define:component name="publicationList"
                          classname="com.arsdigita.london.navigation.ui.object.ComplexObjectList"/>
        <jsp:scriptlet>
        ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) publicationList).setDefinition(new CMSDataCollectionDefinition());
        ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) publicationList).setRenderer(new CMSDataCollectionRenderer());
        ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) publicationList).getDefinition().setObjectType("com.arsdigita.cms.contenttypes.Publication");
        ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) publicationList).getRenderer().setSpecializeObjects(true);
        ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) publicationList).getDefinition().setDescendCategories(true);        
        if((request.getParameterMap().get("DaBInId") != null) &amp;&amp; (((String[])request.getParameterMap().get("DaBInId")).length &gt; 0)) {
          String[] params = (String[]) request.getParameterMap().get("DaBInId");
          String dabinid = params[0];          
          ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) publicationList).setSQLFilter(String.format("authors.pageDescription LIKE '%%DaBInId={%s%%'", dabinid));
          
        } 

        ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) publicationList).getRenderer().setPageSize(99999);
        </jsp:scriptlet>
    </define:page>

    <show:all/>

</jsp:root>
