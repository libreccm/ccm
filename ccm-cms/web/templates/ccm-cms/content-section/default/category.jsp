<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:define="/WEB-INF/bebop-define.tld" 
          xmlns:show="/WEB-INF/bebop-show.tld"
	  version="1.2">

  <define:page name="itemPage" application="content" 
    title="CMS" cache="true">

    <define:component name="categoryIndexPanel"
      classname="com.arsdigita.cms.CategoryIndexPanel"/>
  </define:page>

  <show:all/>
</jsp:root>
