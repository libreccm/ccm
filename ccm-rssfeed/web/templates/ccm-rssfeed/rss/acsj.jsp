<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          version="1.2">

  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
  <jsp:directive.page import="com.arsdigita.rssfeed.ui.FeedGenerator"/>

  <jsp:scriptlet>
    DispatcherHelper.cacheDisable(response);
    new FeedGenerator(true).dispatch(request, response, null);
  </jsp:scriptlet>

</jsp:root>
