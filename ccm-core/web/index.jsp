<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="1.2">

  <jsp:directive.page import="com.arsdigita.ui.UI"/>
  <jsp:directive.page import="com.arsdigita.web.URL"/>
  <jsp:directive.page import="com.arsdigita.web.RedirectSignal"/>
  <jsp:directive.page extends="com.arsdigita.web.BaseJSP"/>

  <jsp:scriptlet>
    throw new RedirectSignal(URL.there(request,UI.getUserRedirectURL(request)),
                             false);
  </jsp:scriptlet>
</jsp:root>
