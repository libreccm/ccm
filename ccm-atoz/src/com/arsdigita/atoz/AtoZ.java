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

package com.arsdigita.atoz;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;

import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainCollection;

import com.arsdigita.web.Application;

import com.arsdigita.xml.Element;

import com.arsdigita.util.Assert;

import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * Base class of the AtoZ application (module)
 * 
 */
public class AtoZ extends Application {

    /** A logger instance to assist debugging.                                */
    private static final Logger logger = Logger.getLogger(AtoZ.class);
    /** PDL Stuff - Base object                                               */
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.atoz.AtoZ";
        
    /* Convenient Strings                                                     */
    public static final String PROVIDERS = "atozProviders";
    public static final String SORT_KEY = "sortKey";

    /** Config object containing various parameter    */
    private static final AtoZConfig s_config = AtoZConfig.getConfig();

    /**
     * Constructor 
     * 
     * @param obj 
     */
    public AtoZ(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor
     * 
     * @param oid 
     */
    public AtoZ(OID oid) {
        super(oid);
    }
    
    /** 
     * Provides client classes with the config object.
     */
    public static AtoZConfig getConfig() {
        return s_config;
    }

    public void addProvider(AtoZProvider provider) {
        DataObject link = add(PROVIDERS, provider);
        // a little insert even magic generates this
        //link.set(SORT_KEY, new Integer(1));
    }

    public void removeProvider(AtoZProvider provider) {
        remove(PROVIDERS, provider);
    }

    public DomainCollection getProviders() {
        DataCollection providers = (DataCollection)get(PROVIDERS);
        providers.addOrder("link." + SORT_KEY);
        return new DomainCollection(providers);
    }

    public AtoZGenerator[] getGenerators() {
        DataCollection providers = (DataCollection)get(PROVIDERS);
        
        List generators = new ArrayList();
        while (providers.next()) {
            AtoZProvider provider = (AtoZProvider)DomainObjectFactory
                .newInstance(providers.getDataObject());
            generators.add(provider.getGenerator());
        }
        
        return (AtoZGenerator[])generators.toArray(
            new AtoZGenerator[generators.size()]);
    }


    public static Element newElement(String name) {
        Assert.isTrue(name.indexOf(":") == -1, "name does not contain :");
        return new Element("atoz:" + name,
                           "http://xmlns.redhat.com/atoz/1.0");
    }

//  /*
//   * Application specific method only required if installed in its own
//   * web application context
//   */
//  public String getContextPath() {
//      return "/ccm-ldn-atoz";
//  }

    /**
     * Returns the path name of the location of the applications servlet/JSP.
     *
     * The method overwrites the super class to provide an application specific
     * location for servlets/JSP. This is necessary if you whish to install the
     * module (application) along with others in one context. If you install the
     * module into its own context (no longer recommended for versions newer
     * than 1.0.4) you may use a standard location.
     *
     * Usually it is a symbolic name/path, which will be mapped in the web.xml
     * to the real location in the file system. Example:
     * <servlet>
     *   <servlet-name>atoz-files</servlet-name>
     *   <servlet-class>com.arsdigita.web.ApplicationFileServlet</servlet-class>
     *   <init-param>
     *     <param-name>template-path</param-name>
     *     <param-value>/templates/ccm-ldn-atoz</param-value>
     *   </init-param>
     * </servlet>
     *
     * <servlet-mapping>
     *   <servlet-name>atoz-files</servlet-name>
     *   <url-pattern>/ccm-ldn-atoz/files/*</url-pattern>
     * </servlet-mapping>
     *
     * @return path name to the applications servlet/JSP
     */
    @Override
    public String getServletPath() {
        // return "/files";
        return "/ccm-atoz/files";
    }

}

