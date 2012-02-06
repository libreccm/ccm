<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:define="/WEB-INF/bebop-define.tld" 
          xmlns:show="/WEB-INF/bebop-show.tld"
    version="1.2">

  <define:page name="autoCategories" pageClass="com.arsdigita.cms.ui.authoring.EmptyPage" title="autoCategories" cache="true">

    <define:component name="autoTerms"
      classname="com.arsdigita.aplaws.ui.AutoTerms"/>
  </define:page>

  <show:all/>
</jsp:root>
