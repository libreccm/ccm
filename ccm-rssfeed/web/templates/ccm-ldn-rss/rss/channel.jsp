<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          version="1.2">

  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
  <jsp:directive.page import="com.arsdigita.london.rss.ui.ChannelGenerator"/>

  <jsp:scriptlet>
    DispatcherHelper.cacheDisable(response);
    new ChannelGenerator().dispatch(request, response, null);
  </jsp:scriptlet>

</jsp:root>
