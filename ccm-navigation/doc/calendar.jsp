<jsp:root 
  xmlns:jsp="http://java.sun.com/JSP/Page" 
  xmlns:define="/WEB-INF/bebop-define.tld"
  xmlns:show="/WEB-INF/bebop-show.tld"
  version="1.2">

  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
  <jsp:directive.page import="com.arsdigita.bebop.parameters.BigDecimalParameter"/>
  <jsp:directive.page import="com.arsdigita.navigation.Navigation"/>

  <jsp:scriptlet>
    long age = Navigation.getConfig().getIndexPageCacheLifetime();
    if (age == 0) {
      DispatcherHelper.cacheDisable(response);
    } else {
      DispatcherHelper.cacheForWorld(response, (int)age);
    }
  </jsp:scriptlet>

  <define:page name="itemPage" application="navigation"
    title="Navigation" cache="true">
    
    <define:component name="greetingItem"
      classname="com.arsdigita.navigation.ui.GreetingItem"/>
    <define:component name="categoryPath"
      classname="com.arsdigita.navigation.ui.category.Path"/>
    <define:component name="categoryMenu"
      classname="com.arsdigita.navigation.ui.category.Menu"/>
    <define:component name="categoryItemList"
      classname="com.arsdigita.navigation.ui.CalendarBrowser"/>
    <jsp:scriptlet>
      ((com.arsdigita.navigation.ui.CalendarBrowser) categoryItemList).addAttribute("objectType");
      ((com.arsdigita.navigation.ui.CalendarBrowser) categoryItemList).setHowMany(30);
      itemPage.addGlobalStateParam(new BigDecimalParameter("categoryID"));
    </jsp:scriptlet>
  </define:page>

  <show:all/>
</jsp:root>
