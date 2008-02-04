<%@ taglib prefix="define" uri="/WEB-INF/bebop-define.tld" %>
<%@ page import="com.arsdigita.bebop.*" %>
<%@ page import="com.arsdigita.bebop.form.*" %>
<%@ page import="com.arsdigita.bebop.event.*" %>

<define:page name="p" title="Test Page">
  <define:form name="myForm">
    <% myForm.addSubmissionListener(new FormSubmissionListener() {
             public void submitted(FormSectionEvent fse) { 
                System.out.println("form submitted");
             }
         });
     %>
    <define:text name="foo" />
    <define:submit name="bar" label="Submit! (name=bar)"/>
  </define:form>
</define:page>
