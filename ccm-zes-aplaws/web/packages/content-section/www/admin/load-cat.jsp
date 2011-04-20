<jsp:root   xmlns:jsp="http://java.sun.com/JSP/Page"
         xmlns:define="/WEB-INF/bebop-define.tld"
           xmlns:show="/WEB-INF/bebop-show.tld"
              version="1.2">

  <define:page name="childCategories"
          pageClass="com.arsdigita.cms.ui.authoring.EmptyPage"
              title="childCategories"
              cache="true">

    <define:component name="catSubtree"
      classname="com.arsdigita.london.terms.ui.CategorySubtree"/>
  </define:page>

  <show:all/>
</jsp:root>
