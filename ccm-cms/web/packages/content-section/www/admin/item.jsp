<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="1.2">

  <jsp:directive.page import="com.arsdigita.bebop.Page"/>
  <jsp:directive.page import="com.arsdigita.cms.ui.ContentItemPage"/>
  <jsp:directive.page import="com.arsdigita.cms.dispatcher.ContentSectionDispatcher"/>
  <jsp:directive.page import="com.arsdigita.cms.ContentSection"/>
  <jsp:directive.page import="com.arsdigita.cms.dispatcher.Utilities"/>
  <jsp:directive.page import="com.arsdigita.dispatcher.*"/>
  <jsp:directive.page import="com.arsdigita.web.LoginSignal"/>
  <jsp:directive.page import="com.arsdigita.web.Web"/>
  <jsp:directive.page import="org.apache.log4j.Logger"/>
  <jsp:directive.page import="java.util.Date"/>


  <jsp:declaration>
    private static final Logger s_log =
        Logger.getLogger("content-section.www.admin.item.jsp");
    private ContentItemPage itemPage = null;
    private Date timestamp = new Date(0);
  </jsp:declaration>

  <jsp:scriptlet>
    s_log.debug("entered item.jsp's service method");
    // Restore the wrapped request
    request = DispatcherHelper.getRequest();
    DispatcherHelper.cacheDisable(response);

    request = DispatcherHelper.getRequest();

    ContentSection section = 
        ContentSectionDispatcher.getContentSection(request);


    if (Web.getContext().getUser() == null) {
        throw new LoginSignal(request);
    } else if (! ContentSectionDispatcher.checkAdminAccess(request, section)) {
        throw new com.arsdigita.cms.dispatcher.AccessDeniedException();
    }

    // page needs to be refreshed when content types or authoring kits
    // in the section change
    synchronized(this) {
        if (Utilities.getLastSectionRefresh(section).after(timestamp)) {
            s_log.error("refreshing itemPage");
            itemPage = new ContentItemPage();
            itemPage.init();
            timestamp = new Date();
        }
    }

    RequestContext context = DispatcherHelper.getRequestContext(request);
    if(itemPage == null) {
      s_log.error("WARNING: itemPage is NULL");
    }
    else {
      s_log.error("ALL OK: itemPage is not null");
    }      
    itemPage.dispatch(request, response, context);
    s_log.debug("exited item.jsp's service method");
  </jsp:scriptlet>
</jsp:root>



