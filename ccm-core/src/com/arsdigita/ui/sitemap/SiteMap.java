/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.ui.sitemap;

import com.arsdigita.web.Application;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.domain.DataObjectNotFoundException;

import org.apache.log4j.Logger;

/**
 * Sitemap administration application. Currently a trivial
 * subclass of com.arsdigita.web.Application
 * @see com.arsdigita.web.Application
 */
public class SiteMap extends Application {
    
    private static final Logger s_log = Logger.getLogger(SiteMap.class);

    public static final String BASE_DATA_OBJECT_TYPE 
        = "com.arsdigita.ui.sitemap.SiteMap";

    public SiteMap(DataObject obj) {
        super(obj);
    }

    public SiteMap(OID oid) 
        throws DataObjectNotFoundException {

        super(oid);
    }    
}
