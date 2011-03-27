<%@ page import = "com.arsdigita.kernel.permissions.UniversalPermissionDescriptor" %>
<%@ page import = "com.arsdigita.kernel.permissions.PermissionService" %>
<%@ page import = "com.arsdigita.kernel.permissions.PrivilegeDescriptor" %>
<%@ page import = "com.arsdigita.web.LoginSignal" %>
<%@ page import = "org.apache.log4j.Logger" %>
<%@ page import = "com.arsdigita.dispatcher.AccessDeniedException" %>
<%@ page import = "com.arsdigita.london.subsite.Site" %>
<%@ page import = "java.math.BigDecimal" %>
<%@ page import = "com.arsdigita.domain.DataObjectNotFoundException" %>
<%@ page import = "com.arsdigita.persistence.SessionManager" %>
<%@ page import = "com.arsdigita.persistence.DataCollection" %>
<%@ page import = "com.arsdigita.kernel.Kernel" %>
<%@ page import = "com.arsdigita.kernel.Party" %>
<%@ page import = "com.arsdigita.kernel.security.*" %>
<%@ page import = "com.arsdigita.themedirector.Theme" %>
<%@ page import = "com.arsdigita.web.Web" %>
<%@ page import = "javax.security.auth.login.LoginException" %>
<%@ page import = "javax.security.auth.login.FailedLoginException" %>
<%@ page import = "com.arsdigita.themedirector.util.GlobalizationUtil" %>
   
<%! 
    /*
     * This page provides the theme application with a "back door"
     * allowing the site administrator to be able to revert a
     * site's look and feel back to the "default" mode.  This is
     * useful for the situation where a user accidently made a mistake
     * and was able to publish a stylesheet that does not compile.
     * This page allows the administrator to return the site to
     * working order at which point they can fix the stylesheet and
     * reapply the theme to the site.
     *
     * Note that this uses absolutely no bebop or xsl.  This is required
     * because one of the stylesheets is broken.
     */
%>

<%!
    public static String SITE_ID = "siteID";
    private static final Logger s_log = 
        Logger.getLogger(Site.class);

    /**
     *  This is used to check the permissions of the user that is logged in.
     */
    private boolean hasPermission(HttpServletRequest sreq) {
        Party party = Kernel.getContext().getParty();
        if (party == null) {
            return false;
        }

        UniversalPermissionDescriptor universalPermission =
            new UniversalPermissionDescriptor(PrivilegeDescriptor.ADMIN, 
                                              party.getOID());

        boolean hasPermission = 
            PermissionService.checkPermission(universalPermission);
        if (!hasPermission) {
            sreq.setAttribute("NO_PERMISSION", Boolean.TRUE);
        }
        return hasPermission;
    }
%>

<%!
        /**
         *  This returns true if the user is able to log in
         *  and false if there is a problem
         */
    private boolean loginUser(HttpServletRequest req) {
        // the timestamp is still new enough
        String timestamp = req.getParameter("timestamp");
        if (timestamp == null) {
            return false;
        }

        try {
            Credential.parse(timestamp);
        } catch (CredentialException e) {
            req.setAttribute("CREDENTIAL_EXCEPTION", Boolean.TRUE);
            return false;
        }
        
        // log in the user if the username/password work

        String username = req.getParameter("username");
        char[] password = req.getParameter("password").trim().toCharArray();

        try {
            UserContext ctx = Web.getUserContext();

            // attempt to log in user
            ctx.login(username, password, false);
            // the login is complete
            return true;
        } catch (FailedLoginException e) {
            req.setAttribute("FAILED_LOGIN_EXCEPTION", Boolean.TRUE);
            s_log.info("Error logging in with username " + username, e);
        } catch (AccountNotFoundException e) {
            req.setAttribute("ACCOUNT_NOT_FOUND_EXCEPTION", Boolean.TRUE);
            s_log.info("Error logging in with username " + username, e);
        } catch (LoginException e) {
            req.setAttribute("UNKNOWN_EXCEPTION", Boolean.TRUE);
            s_log.info("Error logging in with username " + username, e);
        }
        return false;
    }
%>

<%
    // 1. check to make sure the user is a site wide admin.  
    //    If not, we give them a log in form
        if (!hasPermission(request)) {
            // check to see if the user has tried to log in
            boolean loginSuccess = false;
            if (request.getParameter("timestamp") != null) {
                loginSuccess = loginUser(request);
                if (loginSuccess) {
                    // we still need to make sure the user logged
                    // in as an admin
                    loginSuccess = hasPermission(request);
                    if (!loginSuccess) {
                        request.setAttribute("NO_PERMISSION", Boolean.TRUE);
                    }
                }
            }
            if (!loginSuccess) {
                %> <jsp:include page="undoTheme-login.jsp"/> <%
                return;
            }
        }


/*
            throw new AccessDeniedException(
                "user " + party.getOID() + " doesn't have the " + 
                PrivilegeDescriptor.ADMIN.getName() + " admin privileges");
        }
*/

%>

<%
    // 2. Check to see if a site has been clicked...if so, set the
    //    theme to use the default style.
    String siteID = request.getParameter(SITE_ID);
    if (siteID != null) {
        try {
            Site site = Site.retrieve(new BigDecimal(siteID));
            site.setStyleDirectory(null);
            site.save();
        } catch (DataObjectNotFoundException e) {
            s_log.error("There is no site with the id " + siteID);
            out.println("There is no site with the ID of " + siteID);
            out.println("<p>");
        } catch (NumberFormatException e) {
            s_log.error("Error converting the site id " + siteID + " to " +
                        " a real number.", e);
            out.println("There value for " + SITE_ID + " must be a number.");
            out.println("<p>");
        }
    }
%>

<h3>Current Sites</h3>
<ul>
<%
    // 3. build the list of Sites, each with a link to a page that,
    //    when clicked, will revert the site style
    DataCollection collection = SessionManager.getSession().retrieve(Site.BASE_DATA_OBJECT_TYPE);
    while (collection.next()) {
        Site site = new Site(collection.getDataObject());
        String style = site.getStyleDirectory();
        String prettyName = style;
        try {
            Theme theme = Theme.findByURL(style);
            if (theme != null) {
                prettyName = theme.getTitle();
            }
        } catch (DataObjectNotFoundException e) {
            // This just means it is a custom style directory and not a 
            // Theme and thus there is no pretty name.
        }
%>
<li>
    <b>Name:</b> <%= site.getTitle() %>; <b>Host:</b> <%= site.getHostname() %>; <b>Style: </b>
<%
        if (prettyName == null) {
            out.println(GlobalizationUtil.globalize("theme.undo.default_style").localize());
        } else {
%>
<%= prettyName %> <a href="?<%=SITE_ID%>=<%=site.getID()%>">Revert to Default Style</a>
<%
        }
%>
</li>
<%
    }

%>
</ul>
<hr/>
<%
    Party owner = Kernel.getSystemParty();
%>
<a href="mailto:<%=owner.getPrimaryEmail()%>"><%=owner.getName()%></a>

