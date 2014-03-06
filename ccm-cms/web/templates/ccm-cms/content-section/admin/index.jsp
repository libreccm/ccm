<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="1.2">

  <jsp:directive.page import="com.arsdigita.cms.ui.ContentSectionPage"/>
  <jsp:directive.page import="com.arsdigita.cms.ContentSection"/>
  <jsp:directive.page import="com.arsdigita.cms.ContentSectionServlet"/>
  <jsp:directive.page import="com.arsdigita.cms.dispatcher.Utilities"/>
  <jsp:directive.page import="com.arsdigita.dispatcher.*"/>
  <jsp:directive.page import="com.arsdigita.web.LoginSignal"/>
  <jsp:directive.page import="com.arsdigita.web.Web"/>
  <jsp:directive.page import="java.util.Date"/>

  <jsp:declaration>
    private ContentSectionPage sectionPage = new ContentSectionPage();
  </jsp:declaration>

  <jsp:scriptlet>
    // Restore the wrapped request
    request = DispatcherHelper.getRequest();
    DispatcherHelper.cacheDisable(response);

    ContentSection section = 
      ContentSectionServlet.getContentSection(request);

    if (Web.getWebContext().getUser() == null) {
        throw new LoginSignal(request);
    } else if (! ContentSectionServlet.checkAdminAccess(request, section)) {
        throw new com.arsdigita.cms.dispatcher.AccessDeniedException();
    }


    RequestContext context = DispatcherHelper.getRequestContext(request);
    sectionPage.init();
    sectionPage.dispatch(request, response, context);
  </jsp:scriptlet>
</jsp:root>


