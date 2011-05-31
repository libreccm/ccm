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
    <jsp:directive.page import="com.arsdigita.london.navigation.ui.object.CustomizableObjectList"/>
    <jsp:directive.page import="com.arsdigita.london.navigation.ui.object.CompareFilter"/>

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
                          classname="com.arsdigita.london.navigation.ui.GreetingItem"/>
        <define:component name="categoryPath"
                          classname="com.arsdigita.london.navigation.ui.category.Path"/>
        <define:component name="categoryMenu"
                          classname="com.arsdigita.london.navigation.ui.category.Menu"/>
        <define:component name="itemList"
                          classname="com.arsdigita.london.navigation.ui.object.CustomizableObjectList"/>

      <jsp:scriptlet>
      CustomizableObjectList objList = (CustomizableObjectList) itemList;
      objList.setDefinition(new CMSDataCollectionDefinition());
      objList.setRenderer(new CMSDataCollectionRenderer());
      objList.setCustomName("SciProjectList");
      objList.getDefinition().setObjectType("com.arsdigita.cms.contenttypes.SciProject");
      objList.getDefinition().setDescendCategories(false);
      objList.addTextFilter("title", "title");

      java.util.GregorianCalendar now = new java.util.GregorianCalendar();
      String today = String.format("%d-%02d-%02d", now.get(java.util.GregorianCalendar.YEAR),
                                                   now.get(java.util.GregorianCalendar.MONTH) + 1,
                                                   now.get(java.util.GregorianCalendar.DATE));

      objList.addCompareFilter("projectend", "projectstatus", true, true, false)
        .addOption("ongoing", CompareFilter.Operators.GTEQ, today, true)
        .addOption("finished", CompareFilter.Operators.LT, today, false);
      objList.addSortField("title", "title asc");      
      objList.getDefinition().addOrder(objList.getOrder(request.getParameter("sort")));

      objList.getRenderer().setPageSize(20);
      objList.getRenderer().setSpecializeObjects(true);
      objList.getRenderer().setSpecializeObjectsContext("sciProjectList");

      </jsp:scriptlet>

      <define:component name="assignedTerms"
                        classname="com.arsdigita.london.navigation.ui.CategoryIndexAssignedTerms"/>

    </define:page>
    <show:all/>

</jsp:root>