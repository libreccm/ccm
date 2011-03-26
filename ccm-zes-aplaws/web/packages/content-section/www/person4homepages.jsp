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

    <define:page name="person"
                 application="content"
                 title="person4Homepages"
                 cache="false">

        <define:component name="personList"
                          classname="com.arsdigita.london.navigation.ui.object.ComplexObjectList"/>
        <jsp:scriptlet>
        ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) personList).setDefinition(new CMSDataCollectionDefinition());
        ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) personList).setRenderer(new CMSDataCollectionRenderer());
        ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) personList).getDefinition().setObjectType("com.arsdigita.cms.contenttypes.GenericPerson");
        ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) personList).getRenderer().setSpecializeObjects(true);
        ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) personList).getDefinition().setDescendCategories(true);
        ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) personList).getDefinition().setExcludeIndexObjects(false);
	((com.arsdigita.london.navigation.ui.object.ComplexObjectList) personList).getDefinition().setFilterCategory(false);
        ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) personList).getRenderer().setPageSize(99999);
        if((request.getParameterMap().get("DaBInId") != null) &amp;&amp; (((String[])request.getParameterMap().get("DaBInId")).length &gt; 0)) {
          String[] params = (String[]) request.getParameterMap().get("DaBInId");
          String dabinid = params[0];
          ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) personList).setSQLFilter(String.format("pageDescription LIKE '%%DaBInId={%s}%%'", dabinid));
        }

        </jsp:scriptlet>
    </define:page>

    <show:all/>

</jsp:root>