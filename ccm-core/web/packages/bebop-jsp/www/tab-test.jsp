<%@ taglib prefix="define" uri="/WEB-INF/bebop-define.tld" %>
<%@ taglib prefix="show" uri="/WEB-INF/bebop-show.tld" %>
<%@ page import="com.arsdigita.bebop.*" %>
<%@ page import="com.arsdigita.bebop.form.*" %>
<%@ page import="com.arsdigita.bebop.event.*" %>
<%@ page import="com.arsdigita.sitenode.SiteNodePresentationManager" %>
<%@ page import="com.arsdigita.xml.Document" %>
<%@ page import="com.arsdigita.dispatcher.DispatcherHelper" %>

<% request = DispatcherHelper.restoreRequestWrapper(request); %>

<define:page name="p" title="tab pane test">
  <define:tabbedPane name="tabbedPane">
    <define:tab name="leftForm" label="Left Form">
      <define:form name="left">
         <% left.add(new Label("left")); %>
      </define:form>
    </define:tab>
    <define:tab name="right" label="Right Form">
      <define:form name="right">
        <% right.setEncType("multipart/form-data");
    right.setMethod("POST");
    right.add(new FileUpload("upfile"));
    right.add(new Submit("send"));
    right.addProcessListener(new FormProcessListener() { 
        public void process(FormSectionEvent fse) { 
            System.out.println("Got uploaded file");
        }
    });
        %>
     </define:form>
   </define:tab>
 </define:tabbedPane>
</define:page>

<show:page>
  <show:component name="tabbedPane"/>
</show:page>



