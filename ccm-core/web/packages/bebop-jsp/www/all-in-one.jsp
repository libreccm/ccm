<%@ taglib prefix="define" uri="/WEB-INF/bebop-define.tld" %>
<%@ taglib prefix="show" uri="/WEB-INF/bebop-show.tld" %>
<%@ page import="com.arsdigita.bebop.*" %>
<%@ page import="com.arsdigita.bebop.form.*" %>
<%@ page import="com.arsdigita.bebop.event.*" %>

<%--
all-in-one demo.  
shows definition of page and display (show:...) tags in same page
--%>

<define:page name="p" title="Test Page">
<% 
Form myForm = new Form("myForm");
myForm.addProcessListener(new FormProcessListener() {
    public void process(FormSectionEvent fse) { 
       System.err.println("form process");
    }
});
myForm.add(new TextField("foo"));
myForm.add(new Submit("bar"));
p.add(myForm);
%>
</define:page>

<show:page>
this is a test. Let's see if we can insert the form here:

  <show:form name="myForm">
  <ul>
    <li>input widget: <show:component name="foo" />
    <li>submit: <show:component name="bar" />
  </ul>
  </show:form>

  <p> the advantage of not trying to parse this as XML is that we can
  have badly-formed HTML here: 
    <ul> <li> one <li> two <li> three </ul>

</show:page>