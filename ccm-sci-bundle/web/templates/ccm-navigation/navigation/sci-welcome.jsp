<jsp:root 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:define="/WEB-INF/bebop-define.tld"
    xmlns:show="/WEB-INF/bebop-show.tld"
    version="1.2">

    <!-- JSP template for the welcome / start page using navigation -->

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

    <define:page name="defaultItemPage" application="navigation"
                 title="Navigation" cache="true">

        <define:component name="greetingItem"
                          classname="com.arsdigita.navigation.ui.GreetingItem"/>
        <define:component name="categoryPath"
                          classname="com.arsdigita.navigation.ui.category.Path"/>
        <define:component name="categoryMenu"
                          classname="com.arsdigita.navigation.ui.category.Menu"/>
        <define:component name="itemList"
                          classname="com.arsdigita.navigation.ui.object.SimpleObjectList"/>
        <jsp:scriptlet>
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).setDefinition(new CMSDataCollectionDefinition());
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).setRenderer(new CMSDataCollectionRenderer());
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getDefinition().setObjectType("com.arsdigita.cms.ContentPage");

      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getDefinition().setDescendCategories(false);      
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getDefinition().addOrder("parent.categories.link.sortKey");
      
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().setPageSize(30);
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute("objectType");
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute("title");
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "definition");
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "summary");
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "lead");
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "description");
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "eventDate");
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "startDate" );
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "endDate");
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "newsDate");
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "imageAttachments.caption");
      ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).getRenderer().addAttribute( "imageAttachments.image.id");
        </jsp:scriptlet>
        <define:component name="eventList"
                          classname="com.arsdigita.navigation.ui.object.ComplexObjectList"/>
        <jsp:scriptlet>
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).setDefinition(new CMSDataCollectionDefinition());
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).setRenderer(new CMSDataCollectionRenderer());
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).getDefinition().setObjectType("com.arsdigita.cms.contenttypes.Event");

      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).setSQLFilter("(endDate &gt;= :today and (endTime &gt; :time or endTime is null)) or (endDate is null and startDate &gt;= :today)");

      // Java ist mal wieder kompliziert. Man braucht ein Calender-Object, 
      // damit man Datumsarithmetik betreiben kann. java.util.Calendar ist 
      // allerdings abstract. Deshalb muss man java.util.GregorianCalendar 
      // verwenden. Dann kann man mit der add-Methode verschiedene Felder 
      // manipulieren. Aber Achtung - die add-Method liefert void zurueck. Daher
      // kann man das nicht alles in einer Zeile machen. Also Variablen anlegen.
      // Komplizierter geht's wohl nicht mehr.
      java.util.GregorianCalendar now = new java.util.GregorianCalendar();
      java.util.Date today = (new java.util.GregorianCalendar(now.get(java.util.GregorianCalendar.YEAR),
                                                              now.get(java.util.GregorianCalendar.MONTH), 
                                                              now.get(java.util.GregorianCalendar.DATE))).getTime();
      // Im Event-CT ist das Datum als SQL-Type Date eingetragen, die Uhrzeit 
      // aber als SQL-Typ timestamptz. Leider wird von ccm im letzten
      // das Datum nicht gesetzt, so dass der Timestamp immer eine Uhrzeit am 
      // 1.1.1970 angibt. Das ist ziemlich bescheuert und macht hier diesen
      // kompliezierten Vergleich notwendig. Sonst koennte man einfach mit dem 
      // aktuellen Timestamp vergleichen.
      java.util.Date time  = (new java.util.GregorianCalendar(70,0,1, // this is 01.01.1970 - start of UNIX timestamp
                                                              now.get(java.util.GregorianCalendar.HOUR_OF_DAY),
                                                              now.get(java.util.GregorianCalendar.MINUTE),
                                                              now.get(java.util.GregorianCalendar.SECOND))).getTime();
 
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).setParameter("today", today);
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).setParameter("time", time);

      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).getDefinition().setDescendCategories(true);      
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).getDefinition().addOrder("startDate");
      
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).getRenderer().setPageSize(5);
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).getRenderer().addAttribute("objectType");
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).getRenderer().addAttribute("title");
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).getRenderer().addAttribute("lead");
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).getRenderer().addAttribute("eventDate");
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).getRenderer().addAttribute("launchDate");
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).getRenderer().addAttribute("startDate");
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).getRenderer().addAttribute("endDate");
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).getRenderer().addAttribute( "imageAttachments.caption");
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) eventList).getRenderer().addAttribute( "imageAttachments.image.id");
        </jsp:scriptlet>
        <define:component name="newsList"
                          classname="com.arsdigita.navigation.ui.object.ComplexObjectList"/>
        <jsp:scriptlet>
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) newsList).setDefinition(new CMSDataCollectionDefinition());
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) newsList).setRenderer(new CMSDataCollectionRenderer());
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) newsList).getDefinition().setObjectType("com.arsdigita.cms.contenttypes.NewsItem");

      ((com.arsdigita.navigation.ui.object.ComplexObjectList) newsList).setSQLFilter("newsDate &gt; :oldNewsDate");
      
      // Java ist mal wieder kompliziert. Man braucht ein Calender-Object, damit
      // man Datumsarithmetik betreiben kann. java.util.Calendar ist allerdings
      // abstract. Deshalb muss man java.util.GregorianCalendar verwenden. Dann
      // kann man mit der add-Methode verschiedene Felder manipulieren. 
      // Aber Achtung - die add-Method liefert void zurueck. Daher kann man das
      // nicht alles in einer Zeile machen. Also Variablen anlegen.
      // Komplizierter geht's wohl nicht mehr.
      java.util.GregorianCalendar oldDate = new java.util.GregorianCalendar();
      oldDate.add(java.util.Calendar.MONTH, -2);
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) newsList).setParameter("oldNewsDate", oldDate.getTime());

      ((com.arsdigita.navigation.ui.object.ComplexObjectList) newsList).getDefinition().setDescendCategories(true);
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) newsList).getDefinition().addOrder("newsDate desc");

      ((com.arsdigita.navigation.ui.object.ComplexObjectList) newsList).getRenderer().setPageSize(5);
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) newsList).getRenderer().addAttribute("objectType");
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) newsList).getRenderer().addAttribute("title");
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) newsList).getRenderer().addAttribute("lead");
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) newsList).getRenderer().addAttribute("newsDate");
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) newsList).getRenderer().addAttribute( "imageAttachments.caption");
      ((com.arsdigita.navigation.ui.object.ComplexObjectList) newsList).getRenderer().addAttribute( "imageAttachments.image.id");
        </jsp:scriptlet>

        <define:component name="quickLinks"
                          classname="com.arsdigita.navigation.ui.QuickLinks"/>

        <define:component name="assignedTerms"
                          classname="com.arsdigita.navigation.ui.CategoryIndexAssignedTerms"/>

    </define:page>
    <show:all/>
</jsp:root>
