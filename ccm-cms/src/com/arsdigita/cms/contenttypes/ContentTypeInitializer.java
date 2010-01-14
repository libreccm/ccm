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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.LegacyInitEvent;
import com.arsdigita.db.DbHelper;
import com.arsdigita.domain.xml.TraversalHandler;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.xml.XML;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.search.ContentPageMetadataProvider;
import com.arsdigita.search.MetadataProviderRegistry;
import com.arsdigita.util.StringUtils;
import org.apache.log4j.Logger;


public abstract class ContentTypeInitializer extends CompoundInitializer {
    private static Logger s_log = Logger.getLogger(ContentTypeInitializer.class);
    private final String m_objectType;
    public static final String[] EMPTY_ARRAY = new String[0];

    protected ContentTypeInitializer(final String manifestFile,
                                     final String objectType) {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);


        add(new PDLInitializer
            (new ManifestSource
             (manifestFile,
              new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));

        m_objectType = objectType;
    }

    /**
     * 
     * @param evt Type of initialization
     */
    public void init(LegacyInitEvent evt) {
        super.init(evt);

        final String traversal = getTraversalXML();
        if (!StringUtils.emptyString(traversal)) {
            XML.parseResource
                (traversal,
                 new TraversalHandler());
        }

        try {

            ContentType type = ContentType.findByAssociatedObjectType(m_objectType);

            MetadataProviderRegistry.registerAdapter(
                m_objectType,
                new ContentPageMetadataProvider());

            final String[] stylesheets = getStylesheets();
            for (int i = 0; i < stylesheets.length; i++) {
                String stylesheet = stylesheets[i];
                ContentType.registerXSLFile(type, stylesheet);

            }
        } catch (com.arsdigita.domain.DataObjectNotFoundException e) {
            s_log.debug("Unable to register the stylesheet for " +
                        m_objectType +
                        " because the content type was not found. " + 
                        "This is normal during the load script but " +
                        "should not appear during server startup.");
        }

    }

    /**
     * Should be overwritten by each content type to provide its TraversalXML
     * 
     * @return
     */
    public String getTraversalXML() {
        return "";
    }

    /**
     * Should be overwritten by each content types initializer to provide the
     * correct location of is stylesheets.
     * 
     * @return
     */
    public String[] getStylesheets() {
        return EMPTY_ARRAY;
    }
}
