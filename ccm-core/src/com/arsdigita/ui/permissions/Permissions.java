/*
 * Copyright (C) 2012 Peter Boy <pb@zes.uni-bremen.de> All Rights Reserved.
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

package com.arsdigita.ui.permissions;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.web.Application;

import org.apache.log4j.Logger;


/**
 * Sitemap administration application. Currently a trivial
 * subclass of com.arsdigita.web.Application
 * @see com.arsdigita.web.Application
 */
public class Permissions extends Application {
    
    private static final Logger s_log = Logger.getLogger(Permissions.class);

    public static final String BASE_DATA_OBJECT_TYPE 
                               = "com.arsdigita.ui.permissions.Permissions";

    public Permissions(DataObject obj) {
        super(obj);
    }

    public Permissions(OID oid) 
        throws DataObjectNotFoundException {

        super(oid);
    }    

    /**
     * Getter to retrieve the base database object type name
     *
     * @return base data a object type as String
     */
    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String getServletPath() {
        return "/admin-permissions";
    }

}
