This module provides component which allows it to use an implementation of
the content generator interface provided by the PublicPersonalProfile module 
on an Navigation page. 

Example usages are the components for publications and project lists in the 
modules ccm-sci-personalpublications and ccm-sci-personalprojects.

To use the component create new JSP template like to following:

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
  <jsp:directive.page import="com.arsdigita.cms.publicpersonalprofile.PersonalPublications"/>

  <jsp:scriptlet>
    long age = Navigation.getConfig().getIndexPageCacheLifetime();
    if (age == 0) {
      DispatcherHelper.cacheDisable(response);
    } else {
      DispatcherHelper.cacheForWorld(response, (int)age);
    }
  </jsp:scriptlet>

  <define:page name="defaultItemPage" application="PublicPersonalProfile"
    title="Navigation" cache="true">

    <define:component name="greetingItem"
      classname="com.arsdigita.cms.publicpersonalprofile.ui.PersonalContentComponent"/>
    <define:component name="categoryPath"
      classname="com.arsdigita.navigation.ui.category.Path"/>
    <define:component name="categoryMenu"
      classname="com.arsdigita.navigation.ui.category.Menu"/>
    <jsp:scriptlet>
	((com.arsdigita.cms.publicpersonalprofile.ui.PersonalContentComponent) greetingItem).setGenerator(new PersonalPublications());
    </jsp:scriptlet>

    <define:component name="assignedTerms"
         classname="com.arsdigita.navigation.ui.CategoryIndexAssignedTerms"/>

  </define:page>
  <show:all/>
</jsp:root>
