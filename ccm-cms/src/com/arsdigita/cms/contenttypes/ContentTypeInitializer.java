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
import com.arsdigita.runtime.DomainInitEvent;
/*  import com.arsdigita.runtime.LegacyInitEvent; Legacy Init removed */
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


/** 
 * Provides basic functions required by any specific content type initialization.
 *
 * The initializer of each content type will extend this class and
 * <li>delegate to this constructor providing its own manifest file and Base Data
 * Object Type</li>
 * <li>Overwrite getStylesheets() providing its specific stylesheet(s)</li>
 * <li>optionally overwrite getTraversalXML() it necessary.</li>
 * <li>optionally overwrite init(DomainObjectEvent), first delegating to this
 * init() method for common basic tasks and then add its own specific initialization
 * tasks.</li>
 * </ul>
 * 
 */
public abstract class ContentTypeInitializer extends CompoundInitializer {

    /** Logger object for this class  */
    private static Logger s_log = Logger.getLogger(ContentTypeInitializer.class);
    /**
     * Base Data Object Type (i.e. fully qualified domain class name) of the
     * content type. Will be set by each content type with its own value by
     * constructor.*/
    private final String m_objectType;
    /** Just a placeholder in abstract method */
    public static final String[] EMPTY_ARRAY = new String[0];

    /**
     * Constructor, sets specific manifest file and object type.
     *
     * @param manifestFile
     * @param objectType Base Data Object Type of the content type
     */
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
     * Registers the content type (in a transient store (map) which has to be
     * re-initialized each system startup), essential part of initializing the
     * systems domain coupling machinery.
     *
     * @param evt Type of initialization
     */
    @Override
    public void init(DomainInitEvent evt) {
        super.init(evt);

        // Register an optional traversal adapter for the content type
        final String traversal = getTraversalXML();
        if (!StringUtils.emptyString(traversal)) {
            XML.parseResource
                (traversal,
                 new TraversalHandler());
        }

        // Load and register stylesheets for the content type
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

    // Up to version 6.5 ContentTypeInitilizer used init(LegacyInitEvent) for
    // initialization, even though it actually initializes the domain coupling
    // machinery which is the domain of init(DomainInitEvent). It even didn't
    // use any of the legacy initialization features (enterprise.init file).
    // Switched to domain init because legacy init is deprecated and we will get
    // rid of it. Retained here commented out for documentation purpose during
    // transition of contributed content types.
/*  public void init(LegacyInitEvent evt) {
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
*/
    
    /**
     * Retrieves the content types traversal adapter.
     * Has to be overwritten by each specific content type to provide its
     * TraversalXML if it uses one.
     * 
     * @return Fully qualified file name (relative to docuemnt / context root)
     *         to traversal adapter.
     */
    public String getTraversalXML() {
        return "";
    }

    /**
     * Retrieves a list of style sheets assoziated with a content type.
     * Has to be overwritten by each specific content types initializer to
     * provide the correct location of its stylesheets.
     * 
     * @return List (array) of fully qualified file names (relative to docuemnt /
     *         context root) to content types style sheets.
     */
    public String[] getStylesheets() {
        return EMPTY_ARRAY;
    }
}
