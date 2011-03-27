<%@ page import = "org.apache.log4j.Logger" %>
<%@ page import = "java.math.BigDecimal" %>
<%@ page import = "com.arsdigita.kernel.Kernel" %>
<%@ page import = "com.arsdigita.kernel.KernelHelper" %>
<%@ page import = "com.arsdigita.kernel.Party" %>
<%@ page import = "com.arsdigita.ui.login.LoginHelper" %>
<%@ page import = "com.arsdigita.kernel.security.Credential" %>
<%@ page import = "com.arsdigita.kernel.security.CredentialException" %>
<%@ page import = "com.arsdigita.globalization.GlobalizedMessage" %>
<%@ page import = "com.arsdigita.themedirector.util.GlobalizationUtil" %>

<%!
    /**
     *  This page is a log in form for the administrator.  This
     *  is needed because if the xsl has an error then the odds are that
     *  the admin will not be able to log in via the normal means.  So,
     *  This is a log in form that is only for the administrator and does
     *  not use any xsl or bebop at all.  It does not look pretty but
     *  it is a last resort "back door" that should only be used when
     *  all other methods are unavailable and should only be used by the
     *  system superuser so the looks should not be a problem.
     */

    /**
     *  this is just a convenience method
     */
    GlobalizedMessage getMessage(String key) {
        return new GlobalizedMessage(key, "com.arsdigita.ui.login.LoginResources");
    }
%>

<%
if (Boolean.TRUE.equals(request.getAttribute("CREDENTIAL_EXCEPTION"))) {
    out.println("<b>" + GlobalizationUtil.globalize("theme.undo.login_page_expired").localize() + "</b>");
    out.println("<p>");
}
if (Boolean.TRUE.equals(request.getAttribute("FAILED_LOGIN_EXCEPTION")) || Boolean.TRUE.equals(request.getAttribute("ACCOUNT_NOT_FOUND_EXCEPTION"))) {
    out.println("<b>" + GlobalizationUtil.globalize("theme.undo.incorrect_login").localize() + "</b>");
    out.println("<p>");
}

if (Boolean.TRUE.equals(request.getAttribute("UNKNOWN_EXCEPTION"))) {
    out.println("<b>" + GlobalizationUtil.globalize("theme.undo.login_error") + "</b>");
    out.println("<p>");
}

if (Boolean.TRUE.equals(request.getAttribute("NO_PERMISSION")) && Kernel.getContext().getParty() != null) {
    String[] name = {Kernel.getContext().getParty().getName()};
    out.println("<b>" + GlobalizationUtil.globalize("theme.undo.insufficient_privileges", name).localize() + "</b>");
    out.println("<p>");
}


out.println(GlobalizationUtil.globalize("theme.undo.please_login").localize());
%>

<form name="login" method="POST" action="undoTheme.jsp">
<table>
<tr>
<td>
<%
        if (KernelHelper.emailIsPrimaryIdentifier()){
%>
   <b><%=getMessage("login.userRegistrationForm.email").localize()%></b>
</td><td>
   <input type="text" name="username" size="25"/>
<%
        } else {
%>
   <b><%=getMessage("login.userRegistrationForm.screenName").localize()%></b>
</td><td>
   <input type="text" name="username" size="25"/>
<%
        }
%>
</td>
</tr>
<tr>
<td>
<b><%=getMessage("login.userRegistrationForm.password").localize()%></b>
</td>
<td>
  <input type="password" name="password" size="25"/>
  <input type="hidden" name="timestamp" value="<%=Credential.create("timestamp", 1000 * 300)%>"/>
</td>
</tr>
<tr>
<td colspan="2">
<input type="submit" value="Log In"/>
</td>
</tr>
</table>
</form>
