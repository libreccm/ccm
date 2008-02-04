<%@ taglib prefix="show" uri="/WEB-INF/bebop-show.tld" %>

<%@ include file="test-master-def.jsp" %>

<show:page master="master-page.jsp">
this is a slave page, which will display a form included from within
another (master) page.
  <show:form name="myForm">
    input foo <show:component name="foo" />
    <br>
    input bar <show:component name="bar" />
  </show:form>
</show:page>
