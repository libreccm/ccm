<%@ taglib prefix="show" uri="/WEB-INF/bebop-show.tld" %>

<%@ include file="master-page-def.jsp" %>

<show:page>
<font color="red">This is a master page.  

<br>

preslave form, defined in master-page-def.jsp:

</font>

  <show:form name="preSlaveForm">
      form label for input (pre1): <show:component name="pre1" />
    <show:component name="presubmit" />
  </show:form>

  <hr>

  <show:slave/>
 
  <hr>

  <font color="red">slave done</font>

note that you can also show components from the slave page inside 
the master page.  Just refer to them by name.

</show:page>
