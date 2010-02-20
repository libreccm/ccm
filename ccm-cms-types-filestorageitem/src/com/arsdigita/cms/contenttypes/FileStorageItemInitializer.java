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

import org.apache.log4j.Logger;

/**
 * Initializes the File Storage Item content type.
 *
 * Defines the content type specific properties and just uses the super class
 * methods to register the content type with the (transient) content type store
 * (map).
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: FileStorageItemInitializer.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class FileStorageItemInitializer extends ContentTypeInitializer {

    private static final Logger s_log = 
        Logger.getLogger(FileStorageItemInitializer.class);

    /**
     * Constructor, sets the PDL manifest file and object type string.
     */
    public FileStorageItemInitializer() {
        super("ccm-cms-types-filestorageitem.pdl.mf",
              FileStorageItem.BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Retrieve location of traversal adapter
     * @return
     */
    public String getTraversalXML() {
        return "WEB-INF/traversal-adapters/com/arsdigita/" +
            "cms/contenttypes/FileStorageItem.xml";
    }

    /**
     * Retrieve location of this content types stylesheet.
     * @return
     */
    public String[] getStylesheets() {
        return new String[] { 
            "/static/content-types/com/arsdigita/" + 
            "cms/contenttypes/FileStorageItem.xsl"
        };
    }
}
