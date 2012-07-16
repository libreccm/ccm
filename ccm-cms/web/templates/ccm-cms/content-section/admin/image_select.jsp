<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="1.2">

  <jsp:directive.page import="com.arsdigita.cms.ui.ImageSelectPage"/>
  <jsp:directive.page import="com.arsdigita.cms.ContentSectionServlet"/>
  <jsp:directive.page import="com.arsdigita.cms.ContentSection"/>
  <jsp:directive.page import="com.arsdigita.cms.dispatcher.Utilities"/>
  <jsp:directive.page import="com.arsdigita.dispatcher.*"/>
  <jsp:directive.page import="java.util.Date"/>

  <jsp:declaration>
    private ImageSelectPage imageSelectPage = new ImageSelectPage();
  </jsp:declaration>

  <jsp:scriptlet>
    // Restore the wrapped request
    request = DispatcherHelper.getRequest();
    DispatcherHelper.cacheDisable(response);

    ContentSection section = 
      ContentSectionServlet.getContentSection(request);

    if (! ContentSectionServlet.checkAdminAccess(request, section)) {
      throw new com.arsdigita.cms.dispatcher.AccessDeniedException();
    }

    RequestContext context = DispatcherHelper.getRequestContext(request);
    imageSelectPage.init();
    imageSelectPage.dispatch(request, response, context);
  </jsp:scriptlet>
</jsp:root>
