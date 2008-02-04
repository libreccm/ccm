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
 * The CMS initializer.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: FileStorageItemInitializer.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class FileStorageItemInitializer extends ContentTypeInitializer {

    private static final Logger s_log = 
        Logger.getLogger(FileStorageItemInitializer.class);

    public FileStorageItemInitializer() {
        super("ccm-cms-types-filestorageitem.pdl.mf",
              FileStorageItem.BASE_DATA_OBJECT_TYPE);
    }

    public String getTraversalXML() {
        return "WEB-INF/traversal-adapters/com/arsdigita/" +
            "cms/contenttypes/FileStorageItem.xml";
    }

    public String[] getStylesheets() {
        return new String[] { 
            "/static/content-types/com/arsdigita/" + 
            "cms/contenttypes/FileStorageItem.xsl"
        };
    }
}
