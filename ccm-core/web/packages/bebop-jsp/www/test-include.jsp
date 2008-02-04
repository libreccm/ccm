<%@ taglib prefix="show" uri="/WEB-INF/bebop-show.tld" %>

<show:page pageClass="com.arsdigita.bebop.jsp.ExamplePage">
This page is going to include another page.
<br>
include another page:

<hr>

  <%-- use static @include file=... to get around brokenness in JSP 
    1.1 spec. --%>
  <%@ include file="included.jsp" %>

<hr>

include done.

</show:page>

