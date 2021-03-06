/*
 * Copyright (C) 2012 Peter Boy All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.ui.login;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ElementComponent;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageFactory;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.page.BebopApplicationServlet;
import com.arsdigita.dispatcher.DispatcherConfig;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.security.SecurityConfig;
import com.arsdigita.ui.UI;
import com.arsdigita.web.ReturnSignal;
import com.arsdigita.web.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * Login Application Servlet class, central entry point to create and process
 * the Login application UI.
 *
 * It manages user registration page, new user page, user workspace, logout, and
 * permissions admin pages.
 *
 * It just defines a mapping URL <-> various pages and uses the super class to
 * actually server the pages. Additionally is provides service methods to expose
 * various properties, especially the URL's of public subpages (e.g. logout) and
 * initializes the creation of the UI.
 *
 * @author Peter Boy <pboy@barkhof.uni-bremen.de>
 * @version $Id: LoginServlet.java 2161 2012-03-15 00:16:13Z pboy $
 */
public class LoginServlet extends BebopApplicationServlet {

    /**
     * Logger instance for debugging
     */
    private static final Logger s_log = Logger.getLogger(LoginServlet.class);

    // ////////////////////////////////////////////////////////////////////////
    // Define various URLs to subpages of Login to manage administrative tasks.
    // ////////////////////////////////////////////////////////////////////////
    /**
     * PathInfo into the Login application to access the (optional) newUser *
     * page. Ends with "/" because it is a servlet/directory
     */
    public static final String EDIT_USER_PROFILE_PATH_INFO = "/edit-profile/";

    /**
     * PathInfo into the Login application to access the (optional) newUser *
     * page. Ends with "/" because it is a servlet/directory
     */
    public static final String NEW_USER_PATH_INFO = "/new-user/";

    /**
     * PathInfo into the Login application to access the (optional) newUser *
     * page. Ends with "/" because it is a servlet/directory
     */
    public static final String CHANGE_USER_PASSWORD_PATH_INFO
                               = "/change-password/";

    /**
     * PathInfo into the Login application to access the (optional) newUser
     * page. Ends with "/" because it is a servlet/directory
     */
    public static final String RECOVER_USER_PASSWORD_PATH_INFO
                               = "/recover-password/";

    /**
     * PathInfo into the Login application to access the (optional) newUser
     * page. Ends with "/" because it is a servlet/directory
     */
    public static final String EXPLAIN_PERSISTENT_COOKIES_PATH_INFO
                               = "/explain-persistent-cookies/";

    /**
     * PathInfo into the Login application to access the (optional) newUser
     * page. Ends with "/" because it is a servlet/directory
     */
    public static final String LOGIN_EXPIRED_PATH_INFO = "/login-expired/";

    /**
     * PathInfo into the Login application to access the (optional) newUser
     * page. Ends with "/" because it is a servlet/directory
     */
    public static final String LOGOUT_PATH_INFO = "/logout/";

    /**
     * Base URL of the Login application for internal use, fetched from Login
     * domain class.
     */
    private final static String s_loginURL = Login.LOGIN_PAGE_URL;

    // define namespace URI
    final static String SUBSITE_NS_URI = "http://www.arsdigita.com/subsite/1.0";

    public static final String APPLICATION_NAME = "login";

    /**
     * User extension point used to create the pages to server and setup a URL -
     * page mapping.
     *
     * @throws ServletException
     */
    @Override
    public void doInit() throws ServletException {

        // Allow world caching for pages without authentication,
        // ie, /register, /register/explain-persistent-cookies,
        // /register/login-expired, /register/recover-password
        // NB, although you'd think /register is cachable, it
        // stores a timestamp in the login form :(

        /* Create and add login page (index page of Login application) to the
         * page map. KernelSecurityConfig determines whether to create a link
         * to a NewUserRegistrationForm or to skip.                           */
        put("/",
            buildSimplePage("login.userRegistrationForm.title",
                            buildLogin(),
                            "login"));
        disableClientCaching("/");

        /* Create and add userEditPage to the page map.                       */
        put(EDIT_USER_PROFILE_PATH_INFO,
            buildSimplePage("login.userEditPage.title",
                            new UserEditForm(), "edit"));
        disableClientCaching(EDIT_USER_PROFILE_PATH_INFO);

        /* Determines if a NewUserRegistrationForm has to be created by quering
         * Kernel.getSecurityConfig() and acts appropriately                  */
        if (Kernel.getSecurityConfig().isAutoRegistrationOn()) {
            put(NEW_USER_PATH_INFO,
                buildSimplePage("login.userNewForm.title",
                                new UserNewForm(),
                                "register"));
            disableClientCaching(NEW_USER_PATH_INFO);
        }

        /* Create ExplainPersistentCookiesPage and add to the page map        */
        put(EXPLAIN_PERSISTENT_COOKIES_PATH_INFO,
            buildSimplePage("login.explainCookiesPage.title",
                            new ElementComponent(
                                "subsite:explainPersistentCookies",
                                SUBSITE_NS_URI),
                            "cookies"));

        /* Create ChangeUserPasswordPage and add to the page map              */
        put(CHANGE_USER_PASSWORD_PATH_INFO,
            buildSimplePage("login.changePasswordPage.title",
                            new ChangePasswordForm(),
                            "changepassword"));
        disableClientCaching(CHANGE_USER_PASSWORD_PATH_INFO);

        /* Build the password recover page, retrieve its URL and store in map */
        put(RECOVER_USER_PASSWORD_PATH_INFO,
            buildSimplePage("login.recoverPasswordPage.title",
                            new RecoverPasswordPanel(),
                            "recoverpassword"));

        // Build the login expire page, retrieve its URL and store in map
        put(LOGIN_EXPIRED_PATH_INFO, buildExpiredPage());

        /* Create Logout Page and add to the page map                         */
        put(LOGOUT_PATH_INFO, buildLogOutPage());
        disableClientCaching(LOGOUT_PATH_INFO);

        Page workspace = checkForPageSubClass();
        if (workspace == null) {
            workspace = buildSimplePage("login.workspacePage.title",
                                        new UserInfo(),
                                        "workspace");
        }
        put(UI.getWorkspaceURL(), workspace);  // usually navigation/ or portal/
        disableClientCaching(UI.getWorkspaceURL());

        // special case to handle pvt/home
        // String url = LegacyInitializer.getURL(LegacyInitializer.WORKSPACE_PAGE_KEY);
        // pboy: This page / url seems to be a left over from older versions,
        // currently not contained in source code.
        String url = UI.getWorkspaceURL();
        if (url.equals("pvt/")) {
            put("pvt/home", workspace);
            disableClientCaching("pvt/home");
        }

    }

    /**
     * Check wether a custom base Page class (top-level container for all Bebop
     * components and containersPages) is configured and return the appropriate
     * Page. Here used (only) for UserInfo() workspace.
     *
     * @return Page to use for top-level container for all Bebop components and
     *         containersPage, null to use default class
     */
    private static Page checkForPageSubClass() {
        //check to see if there is subclass of Page defined in Config
        DispatcherConfig dc = DispatcherHelper.getConfig();
        String pageClass = dc.getDefaultPageClass();
        Page p = null;
        if (!pageClass.equals("com.arsdigita.bebop.Page")) {
            try {
                // afraid that we're assuming a no-arg constructor
                Class c = Class.forName(pageClass);
                p = (Page) c.newInstance();
            } catch (Exception e) {
                s_log.error(
                    "Unable to instantiate waf.dispatcher.default_page_class", e);
            }
        }
        return p;
    }

    /**
     * Creates a Page with the given title and body component.
     *
     * @return the new Page
     *
     */
    private static Page buildSimplePage(String title, Component body, String id) {

        Page page = PageFactory.buildPage(APPLICATION_NAME,
                                          new Label(LoginHelper
                                              .getMessage(title)),
                                          id);
        page.add(body);
        page.lock();
        return page;
    }

    /**
     * Creates a page informing the user the login has expired. Provides links
     * to login again, etc.
     *
     * @return Page (login expired info)
     */
    private static Page buildExpiredPage() {
        Page page = PageFactory.buildPage(
            APPLICATION_NAME,
            new Label(LoginHelper.getMessage("login.loginExpiredPage.title"))
        );
        page.add(new SimpleContainer() {

            { // constructor
                add(new Label(LoginHelper.getMessage(
                    "login.loginExpiredPage.before")));
                add(new DynamicLink("login.loginExpiredPage.link",
                                    Login.getLoginPageURL()));
                add(new Label(LoginHelper.getMessage(
                    "login.loginExpiredPage.after")));
                add(new ElementComponent("subsite:explainLoginExpired",
                                         SUBSITE_NS_URI));
            }

        });
        page.lock();
        return page;
    }

    /**
     *
     * @return
     */
    private static Page buildLogOutPage() {
        Page page = PageFactory.buildPage(
            APPLICATION_NAME,
            new Label(LoginHelper.getMessage("Logout"))
        );
        page.addActionListener(new UserLogoutListener());
        page.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                final PageState state = event.getPageState();

                final HttpServletRequest req = state.getRequest();

                final String path = UI.getRootPageURL(req);

                throw new ReturnSignal(req, URL.there(req, path));
            }

        });

        page.lock();
        return page;
    }

    private Component buildLogin() {

        final SecurityConfig securityConfig = SecurityConfig.getConfig();

        if (SecurityConfig.getConfig().getEnableSaml()) {

            final UserLoginForm userLoginForm = new UserLoginForm(
                securityConfig.isAutoRegistrationOn());

            final SamlLoginForm samlLoginForm = new SamlLoginForm();

            final BoxPanel panel = new BoxPanel(BoxPanel.HORIZONTAL);
            panel.add(samlLoginForm);
            panel.add(userLoginForm);

            return panel;
        } else {
            return new UserLoginForm(securityConfig.isAutoRegistrationOn());
        }
    }

    /**
     * Provides an (absolute) URL to a user profile editig page. It is relative
     * to document root without any constant prefix if there is one configured.
     *
     * XXX This implementation starts with a leading slash and ends with a
     * slash. In previous configurations String urls began without a slash in
     * order to be able to provide a full URL which also contains the context
     * part. Since version 5.2 the context part is handled by (new) dispatcher.
     * The leading slash it API change! It's impacts have to be checked.
     * (2011-02)
     *
     * @return url to EditUserProfile page as String
     */
    public static String getEditUserProfilePageURL() {
        return s_loginURL + EDIT_USER_PROFILE_PATH_INFO;
    }

    public static String getChangePasswordPageURL() {
        return s_loginURL + CHANGE_USER_PASSWORD_PATH_INFO;
    }

    /**
     * Provides an (absolute URL) to an optional new user registration page
     * (accessible only if activated). It is relative to document root without
     * any constant prefix if there is one configured.
     *
     * XXX This implementation starts with a leading slash and ends with a
     * slash. In previous configurations String urls began without a slash in
     * order to be able to provide a full URL which also contains the context
     * part. Since version 5.2 the context part is handled by (new) dispatcher.
     * The leading slash it API change! It's impacts have to be checked.
     * (2011-02)
     *
     * @return url to new user registration page as String
     */
    public static String getNewUserPageURL() {
        return s_loginURL + NEW_USER_PATH_INFO;
    }

    /**
     * Provides an absolute URL (leading slash) for a password recovery page. It
     * is relative to document root without any constant prefix if there is one
     * configured.
     *
     * XXX This implementation starts with a leading slash and ends with a
     * slash. In previous configurations String urls began without a slash in
     * order to be able to provide a full URL which also contains the context
     * part. Since version 5.2 the context part is handled by (new) dispatcher.
     * The leading slash it API change! It's impacts have tp be checked.
     * (2011-02)
     *
     * @return url String for new user registration page as String
     */
    public static String getRecoverPasswordPageURL() {
        return s_loginURL + RECOVER_USER_PASSWORD_PATH_INFO;
    }

    /**
     * Provides an absolute URL (leading slash) for a cookie explanation page.
     * It is relative to document root without any constant prefix if there is
     * one configured.
     *
     * XXX This implementation starts with a leading slash and ends with a
     * slash. In previous configurations String urls began without a slash in
     * order to be able to provide a full URL which also contains the context
     * part. Since version 5.2 the context part is handled by (new) dispatcher.
     * The leading slash it API change! It's impacts have tp be checked.
     * (2011-02)
     *
     * @return url String for new user registration page as String
     */
    public static String getCookiesExplainPageURL() {
        return s_loginURL + EXPLAIN_PERSISTENT_COOKIES_PATH_INFO;
    }

    /**
     * Provides an absolute URL (leading slash) for a login expired info page.
     * It is relative to document root without any constant prefix if there is
     * one configured.
     *
     * XXX This implementation starts with a leading slash and ends with a
     * slash. In previous configurations String urls began without a slash in
     * order to be able to provide a full URL which also contains the context
     * part. Since version 5.2 the context part is handled by (new) dispatcher.
     * The leading slash it API change! It's impacts have tp be checked.
     * (2011-02)
     *
     * @return url String for new user registration page as String
     */
    public static String getLoginExpiredPageURL() {
        return s_loginURL + LOGIN_EXPIRED_PATH_INFO;
    }

    /**
     * Provides an absolute URL (leading slash) for the system logout page. It
     * is relative to document root without any constant prefix if there is one
     * configured.
     *
     * XXX This implementation starts with a leading slash and ends with a
     * slash. In previous configurations String urls began without a slash in
     * order to be able to provide a full URL which also contains the context
     * part. Since version 5.2 the context part is handled by (new) dispatcher.
     * The leading slash it API change! It's impacts have tp be checked.
     * (2011-02)
     *
     * @return URL for logout page as String
     */
    public static String getLogoutPageURL() {
        return s_loginURL + LOGOUT_PATH_INFO;
    }

}
