/*
 * Copyright (C) 2010 Peter Boy <pboy@barkhof.uni-bremen.de> All Rights Reserved.
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
package com.arsdigita.cms;

import com.arsdigita.cms.util.PageClassConfigHandler;
// import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.runtime.ConfigError;
// import com.arsdigita.kernel.PackageInstance;
// import com.arsdigita.kernel.PackageType;
// import com.arsdigita.kernel.Stylesheet;
// import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
// import com.arsdigita.persistence.DataQuery;
// import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Sets up the CMS package.
 *
 * @author Peter Boy <pboy@barkhof.uni-bremen.,de>
 * @version $Id:  $
 */
public final class ContentCenterSetup {

    private static Logger s_log = Logger.getLogger(ContentCenterSetup.class);

    /** URL to access the CMS Workspace, by default content-center  */
    final String m_workspaceURL;

    /** Map of URL stubs and resource handler classes for ContentCenterDispatcher
     * to use */
    final String m_contentCenterMap;

    /** Contains mapping of URL (key) to resource handler*/
    private static HashMap s_pageClasses = new HashMap();

    /** Contains mapping of resource(key) to resource handler */
    private static HashMap s_pageURLs = new HashMap();


    /**
     * Constructor
     * 
     * @param workspaceURL
     * @param contentCenterMap
     */
    public ContentCenterSetup( String workspaceURL,
                           String contentCenterMap) {

        m_workspaceURL = workspaceURL;
        m_contentCenterMap = contentCenterMap;

    }

    /** Gives you a mapping of URL (key) to resource handler
     *  Use the returned map like so: map.get("search");
     */
    public static HashMap getURLToClassMap() {
        return s_pageClasses;
    }

    /** Gives you a mapping of resource(key) to resource handler
     *  Use the returned map like so: map.get("com.arsdigita.cms.ui.WorkspacePage");
     */
    public static HashMap getClassToURLMap() {
        return s_pageURLs;
    }

    public void run() {

        // Load the content-center page mappings.
        // Has to be executed at each system startup and is an
        // Initializer task.
        setupContentCenter();

    }


    /**
     * Load the content center page mappings.
     * Mapping stored in hashMaps, must be run during each system startup, so
     * it is an initializer task.
     * @throws InitializationException
     */
    private void setupContentCenter() throws ConfigError {

        final PageClassConfigHandler handler 
              = new PageClassConfigHandler(s_pageClasses, s_pageURLs);

        final ClassLoader loader = Thread.currentThread()
                                         .getContextClassLoader();
        final InputStream input = loader.getResourceAsStream
                                         (m_contentCenterMap.substring(1));

        if (input == null) {
            throw new IllegalStateException(m_contentCenterMap + " not found");
        }

        final InputSource source = new InputSource(input);

        try {
            final SAXParserFactory spf = SAXParserFactory.newInstance();
            final SAXParser parser = spf.newSAXParser();
            parser.parse(source, handler);
        } catch (ParserConfigurationException e) {
            throw new UncheckedWrapperException("error parsing dispatcher config", e);
        } catch (SAXException e) { 
            throw new UncheckedWrapperException("error parsing dispatcher config", e);
        } catch (IOException e) { 
            throw new UncheckedWrapperException("error parsing dispatcher config", e);
        }
    }

}
