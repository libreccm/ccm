<jsp:root 
  xmlns:jsp="http://java.sun.com/JSP/Page" 
  xmlns:define="/WEB-INF/bebop-define.tld"
  xmlns:show="/WEB-INF/bebop-show.tld"
  version="1.2">

  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
  <jsp:directive.page import="com.arsdigita.bebop.parameters.BigDecimalParameter"/>
  <jsp:directive.page import="com.arsdigita.london.navigation.Navigation"/>

  <define:page name="itemPage" application="navigation"
    title="Navigation" cache="true">

    <define:component name="greetingItem"
      classname="com.arsdigita.london.navigation.ui.GreetingItem"/>
    <define:component name="categoryPath"
      classname="com.arsdigita.london.navigation.ui.CategoryPath"/>
    <define:component name="categoryNavRecursive"
      classname="com.arsdigita.london.navigation.ui.CategoryNavRecursive"/>
    <define:component name="itemList"
      classname="com.arsdigita.london.navigation.ui.ItemList"/>
    <define:component name="contentSectionComponent"
      classname="com.arsdigita.cms.ui.ContentSectionComponent"/>
    <jsp:scriptlet>
      ((com.arsdigita.london.navigation.ui.ItemList) itemList).setObjectType( "com.arsdigita.cms.ContentBundle" );
      ((com.arsdigita.london.navigation.ui.ItemList) itemList).setHowMany(99);
      ((com.arsdigita.london.navigation.ui.ItemList) itemList).setDescendCategories(true);      
      ((com.arsdigita.london.navigation.ui.ItemList) itemList).addAttribute("objectType");
      ((com.arsdigita.london.navigation.ui.ItemList) itemList).addAttribute( "title" );
      ((com.arsdigita.london.navigation.ui.ItemList) itemList).addAttribute( "description" );
      <!--((com.arsdigita.london.navigation.ui.GreetingItem) greetingItem).addAttribute( "body" );-->
      ((com.arsdigita.london.navigation.ui.ItemList) itemList).addAttribute( "lead" );
      ((com.arsdigita.london.navigation.ui.ItemList) itemList).addAttribute( "lastModifiedDate" );
      ((com.arsdigita.london.navigation.ui.ItemList) itemList).includeImages(4);
    </jsp:scriptlet>
  </define:page>

  <show:all/>
</jsp:root>
