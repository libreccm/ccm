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
    <jsp:directive.page import="org.apache.log4j.Logger"/>

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
                          classname="com.arsdigita.london.navigation.ui.object.ComplexObjectList"/>
        <jsp:scriptlet>

      org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("SciPublicationListJsp");

      application.log("JSP::Configuring object list...");
      logger.warn("JSP::Configuring object list...");

      ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) itemList).setDefinition(new CMSDataCollectionDefinition());
      ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) itemList).setRenderer(new CMSDataCollectionRenderer());
      ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) itemList).getDefinition().setObjectType("com.arsdigita.cms.contenttypes.Publication");
      ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) itemList).setCustomName("SciPublicationsList");


      ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) itemList).getDefinition().setDescendCategories(false);
      logger.warn(String.format("JSP::orderBy = %s", request.getParameter("orderBy")));
      if((request.getParameter("orderBy") == null)) {
        logger.warn("JSP::Setting list order to 'title'...");
        ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) itemList).getDefinition().addOrder("title");
      } else {
        if("title".equals(request.getParameter("orderBy"))) {
            logger.warn("JSP::Setting list order to 'title'...");
            ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) itemList).getDefinition().addOrder("title");
        } else if("authors".equals(request.getParameter("orderBy"))) {
           logger.warn("JSP::Setting list order to 'authors'...");
           ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) itemList).getDefinition().addOrder("authors asc");
           ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) itemList).getDefinition().addOrder("authors.givenname asc");
           ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) itemList).getDefinition().addOrder("authors.link.editor asc");
        } else if("year".equals(request.getParameter("orderBy"))) {
            logger.warn("JSP::Setting list order to 'yearOfPublication'...");
            ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) itemList).getDefinition().addOrder("yearOfPublication asc");
        } else {
            logger.warn("JSP::Unknown value for order, setting list order to 'title'...");
            ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) itemList).getDefinition().addOrder("title");
        }
      }

      ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) itemList).getRenderer().setPageSize(20);
      ((com.arsdigita.london.navigation.ui.object.ComplexObjectList) itemList).getRenderer().setSpecializeObjects(true);
        </jsp:scriptlet>

        <define:component name="assignedTerms"
                          classname="com.arsdigita.london.navigation.ui.CategoryIndexAssignedTerms"/>

    </define:page>
    <show:all/>


</jsp:root>
