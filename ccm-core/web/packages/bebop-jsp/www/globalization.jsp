<%@ taglib prefix="i18n" uri="/WEB-INF/i18n.tld" %>
<%@ page import="com.arsdigita.dispatcher.DispatcherHelper" %>

This is a demonstration of the Jakarta i18n taglib.
<p>
First we need to establish the bundle/locale with the bundle tag.

<i18n:bundle baseName="com.arsdigita.ui.login.SubsiteResources"
             locale="<%= DispatcherHelper.getRequestContext(request).getLocale() %>"
             id="subsite"/>

<p>This is a message from the login package: 

<font color="red">
<i18n:message key="login.userNewForm.passwordBlurb"/>
</font>

<p>This is what happens when you ask for a message that isn't there:
<font color="red">
<i18n:message key="login.userNewForm.asdkfjljkzxcv">
Default message text
</i18n:message>
</font>





