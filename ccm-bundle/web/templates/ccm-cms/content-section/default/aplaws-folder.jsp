<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:define="/WEB-INF/bebop-define.tld" 
          xmlns:show="/WEB-INF/bebop-show.tld"
	  version="1.2">

  <jsp:directive.page import="com.arsdigita.web.URL"/>
  <jsp:directive.page import="com.arsdigita.web.RedirectSignal"/>

  <jsp:scriptlet>
//    throw new RedirectSignal(URL.there(request, "/navigation/"), false);
    throw new RedirectSignal(URL.there(request, "/portal/"), false);
  </jsp:scriptlet>
</jsp:root>
