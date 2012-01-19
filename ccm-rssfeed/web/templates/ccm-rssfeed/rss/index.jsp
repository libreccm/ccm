<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          version="1.2">

  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
  <jsp:directive.page import="com.arsdigita.rssfeed.ui.ChannelIndex"/>

  <jsp:scriptlet>
    DispatcherHelper.cacheDisable(response);
    new ChannelIndex().dispatch(request, response, null);
  </jsp:scriptlet>

</jsp:root>
