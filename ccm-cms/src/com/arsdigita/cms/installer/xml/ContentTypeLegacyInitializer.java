/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.installer.xml;


import com.arsdigita.cms.LoaderConfig;
import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.kernel.BaseInitializer;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.xml.XML;

import java.util.Iterator;
import java.util.List;

//  This initializer loads definitions into the database and is a
//  LOADER TASK!  (pboy)

/**
 * Parses XML file definition of content types and loads them to the
 * database.  The XML config looks like the example below, the
 * "parentType" and "name" attributes are optional, and only required
 * for creating User Defined ContentTypes. Label corresponds to
 * ContentType's label and can be multiple words, and "name" to
 * DynamicObject's name property, and must be a single word. The
 * objectType attribute is ignored for UDCTs, as it gets dynamically
 * generated.
 *
 * <b>UDCT Copyright</b>
 * <pre>
 * &lt;ccm:content-types&gt;
 *   &lt;ccm:content-type 
 *             name="Copyright"
 *             label="UDCT Copyright"
 *             parentType="com.arsdigita.cms.contenttypes.Address"
 *             classname="com.arsdigita.cms.contenttypes.Address"
 *             description="Copyright for storing copyright information" 
 *             objectType="com.arsdigita.cms.contentTypes.Address" &gt;
 *    
 *      &lt;ccm:authoring-kit&gt;
 *      &lt;/ccm:authoring-kit&gt;
 *   &lt;/ccm:content-type&gt;
 * &lt;/ccm:content-types&gt;
 *</pre>
 *
 * <b>Initializer</b>
 * <pre>
 *  init com.arsdigita.cms.installer.ContentTypeInitializer {
 *      contentTypes = {
 *          "/WEB-INF/content-types/Article.xml"
 *      };
 *  }
 *</pre>
 * @author Nobuko Asakai <nasakai@redhat.com>
 * @see XMLContentTypeHandler
 */
public class ContentTypeLegacyInitializer extends BaseInitializer {

    private static org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(ContentTypeLegacyInitializer.class);

    // Load main CMS configuration file
    private static final LoaderConfig s_conf = new LoaderConfig();

//  public static final String CONTENT_TYPES = "contentTypes";
//  private Configuration m_conf = new Configuration();

    public ContentTypeLegacyInitializer() {
        // Could make this a list later...
//      m_conf.initParameter
//          (CONTENT_TYPES,
//           "Path to XML files that contains content type definition",
//           List.class);
    }

    public Configuration getConfiguration() {
        // return m_conf;
        return null;
    }

    protected void doShutdown() {
        // Do nothing
    }

    protected void doStartup() throws InitializationException {
        // List contentTypes = (List)m_conf.getParameter(CONTENT_TYPES);
        List contentTypes = s_conf.getCTDefFiles();
        
        if ( contentTypes != null) {            
            Iterator i = contentTypes.iterator();
            while (i.hasNext()) {
                TransactionContext txn = SessionManager.getSession()
                    .getTransactionContext();
                txn.beginTxn();
                String xmlFile = (String)i.next();
                s_log.debug("Processing contentTypes in: " + xmlFile);
                XML.parseResource(xmlFile, new XMLContentTypeHandler());
                txn.commitTxn();
            }
        }
    }
}
