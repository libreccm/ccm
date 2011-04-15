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
package com.arsdigita.portalserver.admin;

// import com.arsdigita.portalserver.*;
import com.arsdigita.web.Application;
import com.arsdigita.persistence.*;
import org.apache.log4j.Logger;

/**
 * PSAdmin
 * -
 * This class is a mountable application for general Portalserver
 * Administration tasks. It is initialized as /portal-admin,
 * and its UI component is com.arsdigita.portalserver.admin.ui.AdminPage.java
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2004/08/17 $
 * @version Id: PSAdmin.java  pboy $
 */
public class PSAdmin extends Application {

    private static final Logger s_log = Logger.getLogger(PSAdmin.class);

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.admin.CWAdmin";

    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public PSAdmin(DataObject dataObject) {
        super(dataObject);
    }
}
