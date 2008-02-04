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
package com.arsdigita.ui.admin;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.web.ApplicationType;

import org.apache.log4j.Logger;

/**
 * Site wide admin application. Currently a trivial
 * subclass of com.arsdigita.web.Application
 * @see com.arsdigita.web.Application
 */
public class Admin extends Application {

    private static final Logger s_log = Logger.getLogger(Admin.class);

    public static final String BASE_DATA_OBJECT_TYPE
        = "com.arsdigita.ui.admin.Admin";

    public Admin(DataObject obj) {
        super(obj);
    }

    public Admin(OID oid)
        throws DataObjectNotFoundException {

        super(oid);
    }

    /**
     * Returns an instance of the Admin application.  May return null.
     **/
    public static Application getInstance() {
        ApplicationType adminType = ApplicationType.
            retrieveApplicationTypeForApplication(BASE_DATA_OBJECT_TYPE);
        if ( adminType == null ) { return null; }

        ApplicationCollection apps = Application.retrieveAllApplications();
        apps.addEqualsFilter("resourceType.id", adminType.getID());
        if ( !apps.next() ) { return null; }

        Application result = apps.getApplication();
        apps.close();
        return result;
    }
}
