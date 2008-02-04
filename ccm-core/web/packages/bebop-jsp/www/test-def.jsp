<%@ taglib prefix="define" uri="/WEB-INF/bebop-define.tld" %>
<%@ page import="com.arsdigita.bebop.*" %>
<%@ page import="com.arsdigita.bebop.parameters.*" %>
<%@ page import="com.arsdigita.bebop.form.*" %>
<%@ page import="com.arsdigita.bebop.event.*" %>

<define:page name="p" title="Test Page">
<% 
Form myForm = new Form("myForm");
final ServletRequest req = request;
myForm.addProcessListener(new FormProcessListener() {
    public void process(FormSectionEvent fse) { 
       req.setAttribute("test-def.submitted", 
             fse.getFormData().getString("foo"));
    }
});
final StringParameter spFoo = new StringParameter("foo");
myForm.add(new TextField(spFoo));
myForm.add(new Submit("bar"));
p.add(myForm);
%>
</define:page>
