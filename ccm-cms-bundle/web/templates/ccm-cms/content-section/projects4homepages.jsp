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

    <define:page name="publications"
                 application="content"
                 title="Publications4Homepages"
                 cache="false">

        <define:component name="projectList"
                          classname="com.arsdigita.navigation.ui.object.ComplexObjectList"/>
        <jsp:scriptlet>
        ((com.arsdigita.navigation.ui.object.ComplexObjectList) projectList).setDefinition(new CMSDataCollectionDefinition());
        ((com.arsdigita.navigation.ui.object.ComplexObjectList) projectList).setRenderer(new CMSDataCollectionRenderer());
        ((com.arsdigita.navigation.ui.object.ComplexObjectList) projectList).getDefinition().setObjectType("com.arsdigita.cms.contenttypes.SciProject");
        ((com.arsdigita.navigation.ui.object.ComplexObjectList) projectList).getRenderer().setSpecializeObjects(true);
        ((com.arsdigita.navigation.ui.object.ComplexObjectList) projectList).getDefinition().setDescendCategories(true);
        ((com.arsdigita.navigation.ui.object.ComplexObjectList) projectList).getDefinition().setExcludeIndexObjects(false);
	((com.arsdigita.navigation.ui.object.ComplexObjectList) projectList).getDefinition().setFilterCategory(false);
	((com.arsdigita.navigation.ui.object.ComplexObjectList) projectList).getDefinition().setAddOrder("title");
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) projectList).getRenderer().setSpecializeObjectsContext("sciProjectList");
        if((request.getParameterMap().get("DaBInId") != null) &amp;&amp; (((String[])request.getParameterMap().get("DaBInId")).length &gt; 0)) {
          String[] params = (String[]) request.getParameterMap().get("DaBInId");
          String dabinid = params[0];
          ((com.arsdigita.navigation.ui.object.ComplexObjectList) projectList).setSQLFilter(String.format("persons.pageDescription LIKE '%%DaBInId={%s}%%'", dabinid));

        }

        ((com.arsdigita.navigation.ui.object.ComplexObjectList) projectList).getRenderer().setPageSize(99999);
        </jsp:scriptlet>
    </define:page>

    <show:all/>

</jsp:root>
