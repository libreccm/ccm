<%@ taglib prefix="show" uri="/WEB-INF/bebop-show.tld" %>

<%-- basic JSP integration test, taking page definition from test-def --%>
<%@ include file="test-def.jsp" %>

<show:page>
this is a test. Let's see if we can insert the form here:

  <show:form name="myForm">
  <ul>
    <li>input widget: <show:component name="foo" />
    <li>submit: <show:component name="bar" />
  </ul>
  </show:form>

  <% if (request.getAttribute("test-def.submitted") != null) { %>
    <font color="red">form was submitted with value
       <%= request.getAttribute("test-def.submitted") %> </font>
  <% } %>
  <p> the advantage of not trying to parse this as XML is that we can
  have badly-formed HTML here: 
    <ul> <li> one <li> two <li> three </ul>

</show:page>
