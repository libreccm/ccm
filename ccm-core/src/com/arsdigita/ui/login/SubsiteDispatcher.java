/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.bebop.page.BebopMapDispatcher;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ElementComponent;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PageFactory;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.dispatcher.Dispatcher;
import com.arsdigita.dispatcher.DispatcherConfig;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.security.LegacyInitializer;
import com.arsdigita.web.URL;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.ReturnSignal;
import com.arsdigita.web.RedirectSignal;

import java.util.HashMap;
import java.util.Map;
import com.arsdigita.dispatcher.Dispatcher;
import com.arsdigita.dispatcher.DispatcherConfig;
import com.arsdigita.dispatcher.RequestContext;
import java.io.IOException;
import com.arsdigita.dispatcher.DispatcherHelper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;


/**
 * Dispatcher for the login package.  Manages user registration page, new user
 * page, user workspace, logout, and permissions admin pages.
 *
 * @author Sameer Ajmani
 * @version $Id: SubsiteDispatcher.java 1225 2006-06-19 09:27:21Z apevec
 **/
public class SubsiteDispatcher extends BebopMapDispatcher {

	public class SubsiteDispatcherMap extends HashMap implements Map {

		public SubsiteDispatcherMap() {
			super();
			// TODO Auto-generated constructor stub
		}

		
	}

    public static final String APPLICATION_NAME = "login";

    private static final Logger s_log =
        Logger.getLogger(SubsiteDispatcher.class.getName());

    // define namespace URI
    final static String SUBSITE_NS_URI =
        "http://www.arsdigita.com/subsite/1.0";

    /**
     * Initializes dispatcher by registering URLs with bebop pages.
     **/
    public SubsiteDispatcher() {
    	s_log.debug("SubsiteDispatcher is used!!");
        //Map map = new HashMap();
    	Map map = new SubsiteDispatcherMap();
        // special-case the empty URL
        String redirect = LegacyInitializer.getURL(LegacyInitializer.ROOT_PAGE_KEY);
        Dispatcher root = new RedirectDispatcher(redirect);
        map.put("", root);
        map.put("index", root);

        put(map, LegacyInitializer.EDIT_PAGE_KEY, buildSimplePage
            ("login.userEditPage.title", new UserEditForm(), "edit"));
        put(map, LegacyInitializer.LOGIN_PAGE_KEY, buildSimplePage
            ("login.userRegistrationForm.title",
             new UserRegistrationForm(Kernel.getSecurityConfig().isAutoRegistrationOn()),
             "login"));
        if (Kernel.getSecurityConfig().isAutoRegistrationOn()) {
            put(map, LegacyInitializer.NEWUSER_PAGE_KEY, buildSimplePage
                ("login.userNewForm.title", new UserNewForm(),"register"));
        }
        put(map, LegacyInitializer.LOGOUT_PAGE_KEY, buildLogOutPage());
        put(map, LegacyInitializer.COOKIES_PAGE_KEY, buildSimplePage
            ("login.explainCookiesPage.title", new ElementComponent
             ("subsite:explainPersistentCookies", SUBSITE_NS_URI), "cookies"));
        put(map, LegacyInitializer.CHANGE_PAGE_KEY, buildSimplePage
            ("login.changePasswordPage.title", new ChangePasswordForm(),
             "changepassword"));
        put(map, LegacyInitializer.RECOVER_PAGE_KEY, buildSimplePage
            ("login.recoverPasswordPage.title", new RecoverPasswordPanel(),
             "recoverpassword"));

        Page workspace = checkForPageSubClass();
        if (workspace == null) workspace = buildSimplePage
            ("login.workspacePage.title", new UserInfo(), "workspace");
        put(map, LegacyInitializer.WORKSPACE_PAGE_KEY, workspace);
        put(map, LegacyInitializer.EXPIRED_PAGE_KEY, buildExpiredPage());

        // special case to handle pvt/home
        String url = LegacyInitializer.getURL(LegacyInitializer.WORKSPACE_PAGE_KEY);
        if (url.equals("pvt/")) {
            map.put("pvt/home", workspace);
        }

        setMap(map);
    }

    protected void preprocessRequest(HttpServletRequest req,
                                     HttpServletResponse resp,
                                     RequestContext ctx,
                                     String url) {
        // Allow world caching for pages without authentication,
        // ie, /register, /register/explain-persistent-cookies,
        // /register/login-expired, /register/recover-password
        // NB, although you'd think /register is cachable, it
        // stores a timestamp in the login form :(
        if (url.equals(LegacyInitializer.getURL(LegacyInitializer.COOKIES_PAGE_KEY)) ||
            url.equals(LegacyInitializer.getURL(LegacyInitializer.EXPIRED_PAGE_KEY)) ||
            url.equals(LegacyInitializer.getURL(LegacyInitializer.RECOVER_PAGE_KEY))) {
            DispatcherHelper.cacheForWorld(resp);
        } else {
            DispatcherHelper.cacheDisable(resp);
        }
    }

    /**
     * Adds <url, page> to the given map, where URL is looked up from the
     * page map using the given key.  If the URL represents a directory
     * (ends with "/"), URL+"index" is also added to the map and URL-"/" is
     * redirected to URL.
     **/
    private void put(Map map, String key, Page page) {
        String url = LegacyInitializer.getURL(key);
        map.put(url, page);
        if (url.endsWith("/")) {
            map.put(url+"index", page);
            requireTrailingSlash(url.substring(0, url.length()-1));
        }
    }


    private static Page checkForPageSubClass() {
    	//check to see if there is subclass of Page defined in Config
    	DispatcherConfig dc = DispatcherHelper.getConfig();
    	String pageClass = dc.getDefaultPageClass();
    	Page p = null;
    	if (!pageClass.equals("com.arsdigita.bebop.Page")) {
    		try {
    			// afraid that we're assuming a no-arg constructor
    			Class c = Class.forName(pageClass);
    			p = (Page)c.newInstance();
    		} catch (Exception e) {
    			s_log.error("Unable to instantiate waf.dispatcher.default_page_class",e);
    		}
    	}
    	return p;
    }	

    /**
     * Creates a Page with the given title and body component.
     *
     * @return the new Page
     **/
    private static Page buildSimplePage(String title, Component body, String id) {
        Page page = PageFactory.buildPage(
            APPLICATION_NAME,
            new Label(LoginHelper.getMessage(title)),
            id);
        page.add(body);
        page.lock();
        return page;
    }

    private static Page buildExpiredPage() {
        Page page = PageFactory.buildPage(
            APPLICATION_NAME,
            new Label(LoginHelper.getMessage("login.loginExpiredPage.title")));
        page.add(new SimpleContainer() {
             { // constructor
                 add(new Label(LoginHelper.getMessage
                               ("login.loginExpiredPage.before")));
                 add(new DynamicLink("login.loginExpiredPage.link",
                                     LegacyInitializer.LOGIN_PAGE_KEY));
                 add(new Label(LoginHelper.getMessage
                               ("login.loginExpiredPage.after")));
                 add(new ElementComponent("subsite:explainLoginExpired",
                                          SUBSITE_NS_URI));
             }
            });
        page.lock();
        return page;
    }

    private static Page buildLogOutPage() {
        Page page = PageFactory.buildPage(
            APPLICATION_NAME,
            new Label(LoginHelper.getMessage("Logout")));
        page.addActionListener(new UserLogoutListener());
        page.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    final PageState state = event.getPageState();

                    final HttpServletRequest req = state.getRequest();

                    final String path = LegacyInitializer.getFullURL
                        (LegacyInitializer.ROOT_PAGE_KEY, req);

                    throw new ReturnSignal(req, URL.there(req, path));
                }
            });

        page.lock();
        return page;
    }

    private class RedirectDispatcher implements Dispatcher {
        private String m_path;

        public RedirectDispatcher(String path) {
            m_path = path;
        }

        public void dispatch(final HttpServletRequest req,
                             final HttpServletResponse resp,
                             final RequestContext actx)
                throws IOException, ServletException {
            // URL requires that its path argument start with a /,
            // while the old dispatcher stuff assumes paths do not
            // start with a slash.  We translate.

            final URL url = URL.there(req,
                                      "/" + m_path,
                                      new ParameterMap(req));

            throw new RedirectSignal(url, false);
        }
    }
}
