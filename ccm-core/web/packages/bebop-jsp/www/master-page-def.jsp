<%@ taglib prefix="define" uri="/WEB-INF/bebop-define.tld" %>
<%@ page import="com.arsdigita.bebop.*" %>
<%@ page import="com.arsdigita.bebop.form.*" %>
<%@ page import="com.arsdigita.bebop.event.*" %>

<define:page name="masterPage" title="Test Page">
<% 
Form f = new Form("preSlaveForm");
f.add(new TextField("pre1"));
f.add(new Submit("presubmit"));
f.addSubmissionListener(new FormSubmissionListener() { 
   public void submitted(FormSectionEvent fse) { 
      System.out.println("Processed preSlaveForm");
   }
});
masterPage.add(f);
%>
</define:page>