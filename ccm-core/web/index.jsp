<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="1.2">

  <jsp:directive.page import="com.arsdigita.kernel.security.Initializer"/>
  <jsp:directive.page import="com.arsdigita.web.URL"/>
  <jsp:directive.page import="com.arsdigita.web.RedirectSignal"/>
  <jsp:directive.page extends="com.arsdigita.web.BaseJSP"/>

  <jsp:scriptlet>
    throw new RedirectSignal(URL.there(request, 
        Initializer.getFullURL(Initializer.LOGIN_REDIRECT_PAGE_KEY, request)), false);
  </jsp:scriptlet>
</jsp:root>
