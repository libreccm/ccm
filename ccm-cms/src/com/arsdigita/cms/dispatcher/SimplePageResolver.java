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
package com.arsdigita.cms.dispatcher;

import com.arsdigita.cms.ContentSection;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;


/**
 * <p>The default implementation of
 * {@link com.arsdigita.cms.dispatcher.PageResolver}.</p>
 *
 * <p>This implementation uses
 * {@link com.arsdigita.cms.dispatcher.ResourceMapping} to map resources
 * within a content section.</p>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #11 $ $Date: 2004/08/17 $
 * @version $Id: SimplePageResolver.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class SimplePageResolver extends PageResolver {

    private static final Logger s_log = Logger.getLogger
        (SimplePageResolver.class);

    public SimplePageResolver() {
        super();
    }

    /**
     * Fetch the page associated with the request URL.
     *
     * @param url The content section-relative URL stub
     */
    public ResourceHandler getPage(String url) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Getting the resource handler for the url " +
                        "'" + url + "'");
        }

        ResourceHandler page = super.getPage(url);

        if (page == null) {
            s_log.debug("Getting the correct page from the database");

            // Try to read page mapping from the database.

            Session session = SessionManager.getSession();
            DataQuery dq = session.retrieveQuery
                ("com.arsdigita.cms.getResourceMappings");
            Filter f = dq.addFilter("sectionId = :sectionId and url = :url");
            f.set("sectionId", getContentSectionID());
            f.set("url", url);

            String className = null;

            s_log.debug("Getting the page resolver's class name from the " +
                        "database");

            if (dq.next()) {
                className = (String) dq.get("className");
                dq.close();

                if (s_log.isDebugEnabled()) {
                    s_log.debug("Got the class name '" + className +
                                "' from the database");
                }

                try {
                    page = (ResourceHandler) Class.forName(className).newInstance();

                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Loaded instance " + page + "; " +
                                    "initializing it");
                    }

                    page.init();

                    s_log.debug("Storing the page in the resolver cache");

                    loadPage(url, page);
                } catch (ClassNotFoundException cnfe) {
                    throw new UncheckedWrapperException(cnfe);
                } catch (InstantiationException ie) {
                    throw new UncheckedWrapperException(ie);
                } catch (IllegalAccessException iae) {
                    throw new UncheckedWrapperException(iae);
                } catch (ServletException se) {
                    throw new UncheckedWrapperException(se);
                }
            } else {
                s_log.debug("No class name was found");
            }
        }

        return page;
    }

    /**
     * Register a page to the content section.
     *
     * @param page The master page
     * @param url The desired URL of the page
     */
    public void registerPage(ResourceHandler page, String url) {

        Resource resource = null;
        ResourceType type = null;
        ResourceMapping mapping = null;

	ContentSection sec = new ContentSection(getContentSectionID());
	resource = Resource.findResource(sec, url);
	
	if ( resource == null ) {
	    
	    // Create the resource.
	    type = ResourceType.findResourceType("xml");
	    resource = type.createInstance(page.getClass().getName());
	}
	
	mapping = resource.createInstance(sec, url);
	mapping.save();

    }


    /**
     * Register a page to the content section.
     *
     * @param page The master page
     * @param url The desired URL of the page
     */
    public void unregisterPage(ResourceHandler page, String url) {

        Resource resource = null;
        ContentSection sec = new ContentSection(getContentSectionID());
        resource = Resource.findResource(sec, url);

        if ( resource != null ) {
            resource.delete();
        }
    }

}
