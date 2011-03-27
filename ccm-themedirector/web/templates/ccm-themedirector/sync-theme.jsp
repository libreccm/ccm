<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:define="/WEB-INF/bebop-define.tld" 
          xmlns:show="/WEB-INF/bebop-show.tld"
          version="1.2">

  <jsp:directive.page import="com.arsdigita.bebop.parameters.BigDecimalParameter"/>
  <jsp:directive.page import="com.arsdigita.dispatcher.DispatcherHelper"/>
  <jsp:directive.page import="com.arsdigita.themedirector.util.ThemePublishedFileManager"/>
  <jsp:directive.page import="com.arsdigita.themedirector.util.ThemeDevelopmentFileManager"/>
  <jsp:directive.page import="com.arsdigita.themedirector.util.ThemeFileManager"/>
  <jsp:directive.page import="com.arsdigita.themedirector.ThemeFile"/>
  <jsp:directive.page import="com.arsdigita.themedirector.Theme"/>
  <jsp:directive.page import="javax.servlet.jsp.JspWriter"/>
  <jsp:directive.page import="com.arsdigita.domain.DataObjectNotFoundException"/>
  <jsp:directive.page import="com.arsdigita.dispatcher.AccessDeniedException"/>
  <jsp:directive.page import="com.arsdigita.kernel.Party"/>
  <jsp:directive.page import="com.arsdigita.kernel.Kernel"/>
  <jsp:directive.page import="com.arsdigita.kernel.Resource"/>
  <jsp:directive.page import="com.arsdigita.kernel.permissions.PermissionDescriptor"/>
  <jsp:directive.page import="com.arsdigita.kernel.permissions.PrivilegeDescriptor"/>
  <jsp:directive.page import="com.arsdigita.kernel.permissions.PermissionService"/>
  <jsp:scriptlet>
    DispatcherHelper.cacheDisable(response);

      // this is a page that finds the ThreadFileManager and tells it
      // to run immediately.  
      // is there a way to do this without blocking on returning to the user?
      String themeURL = request.getParameter("themeURL");
      Theme theme = null;
        if (themeURL != null) {
            try {
                theme = Theme.findByURL(themeURL);
            } catch (DataObjectNotFoundException e) {
                // no item so we just return
                out.println("Unable to sync theme with URL " + themeURL +
                            " because no theme found");
                return;
            }
        }

        String updateType = request.getParameter("updateType");
        ThemeFileManager pubManager = ThemePublishedFileManager.getInstance();
        ThemeFileManager devManager = ThemeDevelopmentFileManager.getInstance();
        if (pubManager != null &amp;&amp; (updateType == null || ThemeFile.LIVE.equals(updateType))) {
            if (theme == null) {
                pubManager.updateAllThemesNow();
            } else {
                pubManager.updateThemeNow(theme);
            }
        }

        if (devManager != null &amp;&amp; (updateType == null || ThemeFile.DRAFT.equals(updateType))) {
            if (theme == null) {
                devManager.updateAllThemesNow();
            } else {
                devManager.updateThemeNow(theme);
            }
        }
  </jsp:scriptlet>
<jsp:text>
Sync Complete
</jsp:text>
</jsp:root>
