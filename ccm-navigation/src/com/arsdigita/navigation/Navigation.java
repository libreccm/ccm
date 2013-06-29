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
 */
package com.arsdigita.navigation;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.navigation.tools.NavigationCreator;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;


import com.arsdigita.web.Application;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;
import org.apache.log4j.Logger;

public class Navigation extends Application {

    private static final Logger LOGGER = Logger.getLogger(Navigation.class);
    public static final String NAV_NS = "http://ccm.redhat.com/navigation";
    public static final String NAV_PREFIX = "nav";
    public static final String OID = "oid";
    public static final String CAT_ID_REDIRECT = "categoryID";
    private static final NavigationConfig CONFIG = new NavigationConfig();
    private static final NavigationContext CONTEXT = new NavigationContext();

    static {
        LOGGER.debug("Static initalizer starting...");
        CONFIG.load();
        LOGGER.debug("Static initalizer finished.");
    }

    public static NavigationConfig getConfig() {
        return CONFIG;
    }

    public static NavigationContext getContext() {
        return CONTEXT;
    }

    public static Element newElement(final String name) {
        return new Element(NavigationConstants.NAV_PREFIX + ":" + name,
                           NavigationConstants.NAV_NS);
    }

    public static Element newElement(final Element parent, final String name) {
        return parent.newChildElement(String.format("%s:%s", NavigationConstants.NAV_PREFIX, name),
                                      NavigationConstants.NAV_NS);
    }

    public static String redirectURL(final OID oid) {
        final ParameterMap map = new ParameterMap();
        map.setParameter(NavigationConstants.OID, oid.toString());

        final URL here = Web.getContext().getRequestURL();

        return new URL(here.getScheme(),
                       here.getServerName(),
                       here.getServerPort(),
                       "",
                       "",
                       "/redirect/", map).toString();
    }

    public static String redirectURL(final OID oid, final ParameterMap inMap) {

        ParameterMap map;
        try {
            map = (ParameterMap) inMap.clone();
        } catch (CloneNotSupportedException e) {
            map = inMap;
        }
        map.setParameter(NavigationConstants.OID, oid.toString());

        return URL.there(Web.getRequest(), "/redirect/", map).toString();
    }

    /**
     * Creates a new navigation instance. This method was originally part of the {@link NavigationCreator} CLI tool,
     * but was moved here because the logic of this method is needed by at least two other classes.
     * 
     * @param navUrl The URL of the new navigation instance.
     * @param navTitle The title of the new navigation instance.
     * @param defaultDomain The default domain of the new navigation instance.
     */
    public static void createNavigation(final String navUrl, 
                                        final String navTitle, 
                                        final String defaultDomain, 
                                        final String description) {

        if (Application.isInstalled(Navigation.BASE_DATA_OBJECT_TYPE, "/" + navUrl + "/")) {
            throw new IllegalArgumentException(String.format("%s already installed at %s",
                                                             Navigation.BASE_DATA_OBJECT_TYPE,
                                                             navUrl));
        } else {
            DomainObjectFactory.registerInstantiator(Navigation.BASE_DATA_OBJECT_TYPE,
                                                     new DomainObjectInstantiator() {
                @Override
                protected DomainObject doNewInstance(final DataObject dataObject) {
                    return new Navigation(dataObject);
                }

            });
            
            /* Create Instance beyond root (4. parameter null)       */
            final Application application = Application.createApplication(Navigation.BASE_DATA_OBJECT_TYPE, 
                                                                          navUrl, 
                                                                          navTitle, 
                                                                          null);
            application.setDescription(description);
            application.save();
            final Domain termDomain = Domain.retrieve(defaultDomain);
            termDomain.setAsRootForObject(application, null);
        }

    }

    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.navigation.Navigation";

    public Navigation(final DataObject obj) {
        super(obj);
    }

    public Navigation(final OID oid) {
        super(oid);
    }

    /*
     public String getContextPath() {
     return "ccm-navigation";
     }
     */
    /**
     * Returns the path to the location of the applications servlet/JSP.
     *
     * The methods overwrites the super class to provide a application specific
     * location for servlets/JSP.
     *
     * Usually it is a symbolic name/path, which will be mapped in the web.xml
     * to the real location in the file system. You will find the following
     * kind of code:
     * <servlet>
     *   <servlet-name>navigation-files</servlet-name>
     *   <servlet-class>com.arsdigita.web.ApplicationFileServlet</servlet-class>
     *   <init-param>
     *     <param-name>template-path</param-name>
     *     <param-value>/templates/ccm-navigation</param-value>
     *   </init-param>
     *   <init-param>
     *     <param-name>file-resolver</param-name>
     *     <param-value>com.arsdigita.navigation.NavigationFileResolver
     *     </param-value>
     *   </init-param>
     * </servlet>
     *
     * <servlet-mapping>
     *   <servlet-name>navigation-files</servlet-name>
     *   <url-pattern>/ccm-navigation/files/*</url-pattern>
     * </servlet-mapping>
     *
     * @return path to the applications servlet/JSP
     */
    @Override
    public String getServletPath() {
        //return "/files";
        return "/ccm-navigation/files";
    }

}
