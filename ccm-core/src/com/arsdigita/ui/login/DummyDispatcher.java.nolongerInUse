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
// import com.arsdigita.dispatcher.Dispatcher;
// import com.arsdigita.dispatcher.DispatcherConfig;
// import com.arsdigita.dispatcher.DispatcherHelper;
// import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.kernel.Kernel;
// import com.arsdigita.kernel.security.LegacyInitializer;
import com.arsdigita.ui.UI;
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
 * DummyDispatcher for the old acs-subsite package login package. 
 * Has to be deleted as soon as all application modules are legacy free!
 *
 * @author Sameer Ajmani
 * @version $Id: SubsiteDispatcher.java 1225 2006-06-19 09:27:21Z apevec
 **/
public class DummyDispatcher extends BebopMapDispatcher {

    public static final String APPLICATION_NAME = "login";

    private static final Logger s_log =
                         Logger.getLogger(DummyDispatcher.class.getName());

    // define namespace URI
    final static String SUBSITE_NS_URI =
        "http://www.arsdigita.com/subsite/1.0";

    /** Dispatcher map class to store url - page mapping    */
	public class SubsiteDispatcherMap extends HashMap implements Map {

		public SubsiteDispatcherMap() {
			super();
			// TODO Auto-generated constructor stub
		}
	}

    /**
     * Constructor initializes dispatcher by registering URLs with bebop pages.
     */
    public DummyDispatcher() {
    	s_log.debug("SubsiteDispatcher Constructor entered.");

    	Map map = new SubsiteDispatcherMap();

        // special-case the empty URL
//        String redirect = LegacyInitializer.getURL(LegacyInitializer.ROOT_PAGE_KEY);
        String redirect = UI.getRootPageURL();
        Dispatcher root = new RedirectDispatcher(redirect);

        map.put("", root);
        map.put("index", root);


        setMap(map);
    }

    /**
     * Adds <url, page> to the given map. If the URL represents a directory
     * (ends with "/"), URL+"index" is also added to the map and URL-"/" is
     * redirected to URL.
     **/
    private void put(Map map, String url, Page page) {
        // String url = LegacyInitializer.getURL(key);
        if (url.startsWith("/")) {
            // Currently the getter method provide a leading slash (API change)
            // but the dispatcher needs an url without
            // Needs to be checked when the old style sitenode based Dispatcher
            // is eliminated.
            url = url.substring(1);
        }
        map.put(url, page);
        if (url.endsWith("/")) {
            map.put(url+"index", page);
            requireTrailingSlash(url.substring(0, url.length()-1));
        }
    }

    @Override
    protected void preprocessRequest(HttpServletRequest req,
                                     HttpServletResponse resp,
                                     RequestContext ctx,
                                     String url) {
        // Allow world caching for pages without authentication,
        // ie, /register, /register/explain-persistent-cookies,
        // /register/login-expired, /register/recover-password
        // NB, although you'd think /register is cachable, it
        // stores a timestamp in the login form :(
        //
        // url comes without leading "/" and we have to compensate for the
        // leading slash provided by the UI.get... methods here.
        if (("/"+url).equals(UI.getCookiesExplainPageURL()) ||
        //  ("/"+url).equals(UI.getLoginExpiredPageURL())   ||
            ("/"+url).equals(UI.getRecoverPasswordPageURL()) ) {
            DispatcherHelper.cacheForWorld(resp);
        } else {
            DispatcherHelper.cacheDisable(resp);
        }
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
