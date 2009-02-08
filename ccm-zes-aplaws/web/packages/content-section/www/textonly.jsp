<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="1.2">

<jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper" />

<jsp:scriptlet>

  String returnURL = request.getParameter("returnURL");

  if ( returnURL == null ) {
    returnURL = "/";
  }

  String mode = request.getParameter("textOnly");
  if ("1".equals(mode)) {
    if (!returnURL.startsWith("/text")) {
       returnURL = "/text" + returnURL;
    }
  } else {
    if (returnURL.startsWith("/text")) {
       returnURL = returnURL.substring(5);
    }
  }

  response.sendRedirect(returnURL);
</jsp:scriptlet>

</jsp:root>
