<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="1.2">

  <jsp:directive.page import="com.arsdigita.ui.UI"/>
  <jsp:directive.page import="com.arsdigita.web.URL"/>
  <jsp:directive.page import="com.arsdigita.web.RedirectSignal"/>
  <jsp:directive.page extends="com.arsdigita.web.BaseJSP"/>
  <jsp:directive.page import="com.arsdigita.cms.ContentSection"/>
  <jsp:directive.page import="com.arsdigita.cms.ContentSectionCollection"/>
  <jsp:directive.page import="com.arsdigita.cms.SecurityManager"/>
  <jsp:directive.page import="com.arsdigita.cms.dispatcher.Utilities"/>
  <jsp:directive.page import="com.arsdigita.cms.Workspace"/>

  <jsp:scriptlet>
    ContentSectionCollection sections = ContentSection.getAllSections();
    boolean hasAccess = false;
    while (sections.next()) {
        ContentSection section = sections.getContentSection();
        SecurityManager sm = new SecurityManager(section);
        if (sm.canAccess(request, SecurityManager.ADMIN_PAGES)) {
            hasAccess = true;
            break;
        }
    }
    sections.close();

    String url;
    if (hasAccess) {
        // url = Utilities.getWorkspaceURL();
        url = Workspace.getURL();
    } else {
        url = UI.getWorkspaceURL(request);
    }

    throw new RedirectSignal(URL.there(request, url), false);
  </jsp:scriptlet>
</jsp:root>
