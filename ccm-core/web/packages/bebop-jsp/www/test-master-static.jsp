<%@ taglib prefix="show" uri="/WEB-INF/bebop-show.tld" %>

<%@ include file="test-master-def.jsp" %>

<show:page>
<%@ include file="static-header.jsp" %>

this is a slave page, which will display a form included from within
another (master) page.
  <show:form name="myForm">
    input foo <show:component name="foo" />
    <br>
    input bar <show:component name="bar" />
  </show:form>

<%@ include file="static-footer.jsp" %>
</show:page>

