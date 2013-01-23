<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="1.2">

  <jsp:directive.page import="com.arsdigita.ui.UI"/>
  <jsp:directive.page import="com.arsdigita.web.Web"/>
  <jsp:directive.page import="com.arsdigita.web.WebContext"/>
  <jsp:directive.page import="com.arsdigita.web.URL"/>
  <jsp:directive.page import="com.arsdigita.web.RedirectSignal"/>
  <jsp:directive.page extends="com.arsdigita.web.BaseJSP"/>

  <jsp:scriptlet>
    // throw new RedirectSignal(URL.there(request,UI.getUserRedirectURL(request)),
    //                         false);
    if (Web.getContext().getUser() == null) {
      // User not logged in, display public front page
      throw new RedirectSignal(URL.there(request,UI.getWorkspaceURL(request)),
                               false);
    } else {
      // User logged in, redirect to user redirect page
      throw new RedirectSignal(URL.there(request,UI.getUserRedirectURL(request)),
                               false);
    }
  </jsp:scriptlet>
</jsp:root>
