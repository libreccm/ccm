<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:define="/WEB-INF/bebop-define.tld" 
          xmlns:show="/WEB-INF/bebop-show.tld"
          version="1.2">

  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
  <jsp:directive.page import="com.arsdigita.toolbox.ui.ApplicationAuthenticationListener"/>

  <jsp:scriptlet>
    DispatcherHelper.cacheDisable(response);
  </jsp:scriptlet>

  <define:page name="authPage" application="auth-http" 
    title="HTTP Authentication Admin" cache="true">

    <jsp:scriptlet>
      authPage.addRequestListener(new ApplicationAuthenticationListener());
    </jsp:scriptlet>

    <define:tabbedPane name="tabs">
       <define:tab name="edittab" label="Edit a user">
         <define:component name="edit" classname="com.arsdigita.auth.http.ui.EditUserPane"/>
       </define:tab>
       <define:tab name="addtab" label="Add a user">
         <define:component name="add" classname="com.arsdigita.auth.http.ui.AddUserPane"/>
       </define:tab>
       <define:tab name="bulktab" label="Bulk Import">
         <define:component name="bulk" classname="com.arsdigita.auth.http.ui.UpdateUsersForm"/>
       </define:tab>
    </define:tabbedPane>
  </define:page>

  <show:all/>
</jsp:root>
