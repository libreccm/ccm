<%@ taglib prefix="show" uri="/WEB-INF/bebop-show.tld" %>

<%-- basic JSP integration test, taking page definition from class  --%>
<show:page pageClass="com.arsdigita.bebop.jsp.ExamplePage">

Showing a page from a class.

  <show:form name="exampleForm">
  <ul>
    <li>first name field: <show:component name="textField" />
    <li>last name field: <show:component name="submitWidget" />
  </ul>
  </show:form>

</show:page>