<!--
   Template to generate a list of events

   ##Title: Events Page
   ##Descr: Generates an event list ordering items in ??? order.
   ##Path : /templates/ccm-navigation/navigation/def-events.jsp
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
    int maxItems = Navigation.getConfig().getIndexPageMaxItems();
  </jsp:scriptlet>

  <define:page name="defaultItemPage" application="navigation"
    title="Navigation" cache="true">

    <define:component name="greetingItem"
                      classname="com.arsdigita.navigation.ui.GreetingItem"/>
    <define:component name="categoryPath"
                      classname="com.arsdigita.navigation.ui.category.Path"/>

    <define:component name="categoryMenu"
                      classname="com.arsdigita.navigation.ui.category.Menu"/>
    // Menu for mobile (responsive) Version theme UniHB
    <define:component name="categoryNav"
                      classname="com.arsdigita.navigation.ui.category.Hierarchy">
        <jsp:scriptlet>
            ((com.arsdigita.navigation.ui.category.Hierarchy) categoryNav).setShowItems(false);
        </jsp:scriptlet>
    </define:component>


    <define:component name="eventList"
                      classname="com.arsdigita.navigation.ui.object.ComplexObjectList"/>

    <jsp:scriptlet>
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).setDefinition(new CMSDataCollectionDefinition());
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).setRenderer(new CMSDataCollectionRenderer());
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).getDefinition().setObjectType("com.arsdigita.cms.contenttypes.Event");

      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).setSQLFilter("(endDate &gt;= :today and (endTime &gt; :time or endTime is null)) or (endDate is null and startDate &gt;= :today)");

      // Java needs a java.util.GregorianCalendar object to be able to manipulate various fields using
      // the add method. The add method returns void, so we need an additional variable.
      java.util.GregorianCalendar now = new java.util.GregorianCalendar();
      java.util.Date today = (new java.util.GregorianCalendar(now.get(java.util.GregorianCalendar.YEAR),
                                                              now.get(java.util.GregorianCalendar.MONTH),
                                                              now.get(java.util.GregorianCalendar.DATE))).getTime();
      // The Event content type does use a SQL type for date, but the time as SQL-Typ timestamptz.  
      // Unfortunately, ccm does not set the date in the last step, therefore timestamp date is 
      // always 1970-01-01. So we have to use a rather complicated comparison here.
      java.util.Date time  = (new java.util.GregorianCalendar(70,0,1, // this is 01.01.1970 - start of UNIX timestamp
                                                              now.get(java.util.GregorianCalendar.HOUR_OF_DAY),
                                                              now.get(java.util.GregorianCalendar.MINUTE),
                                                              now.get(java.util.GregorianCalendar.SECOND))).getTime();

      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).setParameter("today", today);
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).setParameter("time", time);

      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).getDefinition().setDescendCategories(true);
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).getDefinition().addOrder("startDate");

      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().setPageSize(maxItems);
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).getRenderer().addAttribute("objectType");
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).getRenderer().addAttribute("title");
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).getRenderer().addAttribute("lead");
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).getRenderer().addAttribute("eventDate");
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).getRenderer().addAttribute("launchDate");
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).getRenderer().addAttribute("startDate");
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).getRenderer().addAttribute("endDate");
</jsp:scriptlet>

    <define:component name="assignedTerms"
         classname="com.arsdigita.navigation.ui.CategoryIndexAssignedTerms"/>

  </define:page>
  <show:all/>
</jsp:root>
