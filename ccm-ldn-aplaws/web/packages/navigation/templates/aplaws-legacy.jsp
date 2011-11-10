<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:define="/WEB-INF/bebop-define.tld" 
          xmlns:show="/WEB-INF/bebop-show.tld"
	  version="1.2"> 

    <jsp:directive.page import="
	com.arsdigita.cms.contenttypes.Article,
	com.arsdigita.navigation.ui.CategoryItemNav,
	com.arsdigita.navigation.ui.CategoryNav,
	com.arsdigita.navigation.ui.ItemList"/>
<jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>

<jsp:scriptlet>DispatcherHelper.cacheForWorld( response );</jsp:scriptlet>

<define:page name="itemPage" application="content-section"
  title="APLAWS" cache="true">

<define:component name="categoryNav"
    classname="com.arsdigita.navigation.ui.CategoryNav">
  <jsp:scriptlet> 
  ((CategoryNav) categoryNav).setUseItemID(false); 
  </jsp:scriptlet>
</define:component>

<define:component name="categoryItemNav"
    classname="com.arsdigita.navigation.ui.CategoryItemNav">
  <jsp:scriptlet> 
  ((CategoryItemNav) categoryItemNav).setUseItemID(false); 
  </jsp:scriptlet>
</define:component>

<define:component name="greetingComponent"
    classname="com.arsdigita.navigation.ui.GreetingItem">
</define:component>

<define:component name="fixedPromoComponent"
    classname="com.arsdigita.navigation.ui.ItemList">
  <jsp:scriptlet>
  ItemList fixedPromo = (ItemList) fixedPromoComponent;
  fixedPromo.setObjectType( Article.BASE_DATA_OBJECT_TYPE );
  fixedPromo.setSpecificObjectType
  	( "com.arsdigita.london.cms.dublin.types.FixedPromo" );
  fixedPromo.addAttribute( "title" );
  fixedPromo.addAttribute( "lead" );
  fixedPromo.setHowMany( 2 );
  </jsp:scriptlet>
</define:component>

<define:component name="dynamicPromoComponent"
    classname="com.arsdigita.navigation.ui.ItemList">
  <jsp:scriptlet>
  ItemList dynamicPromo = (ItemList) dynamicPromoComponent;
  dynamicPromo.setObjectType( Article.BASE_DATA_OBJECT_TYPE );
  dynamicPromo.addAttribute( "name" );
  dynamicPromo.addAttribute( "title" );
  dynamicPromo.addAttribute( "lead" );
  dynamicPromo.addExcludedType
  	( "com.arsdigita.london.cms.dublin.types.FixedPromo" );
  dynamicPromo.setHowMany( 3 );
  </jsp:scriptlet>
</define:component>

<define:component name="L3ContentPane"
    classname="com.arsdigita.navigation.ui.L3ContentPane">
</define:component>

<define:component name="relatedItemsComponent"
    classname="com.arsdigita.navigation.ui.RelatedItems">
</define:component>

</define:page>

<show:all/>

</jsp:root>
