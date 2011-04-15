/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.portalserver.ui.admin;

import com.arsdigita.web.Application;
import com.arsdigita.persistence.*;
import org.apache.log4j.Logger;

/**
 * PortalCreator
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2004/08/17 $
 */
public class PortalCreator extends Application {
    public static final String versionId =
        "$Id: //portalserver/dev/src/com/arsdigita/portalserver/ui/admin/PortalCreator.java#5 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/08/17 23:19:25 $";

    private static final Logger s_log = Logger.getLogger
        (PortalCreator.class);

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.workspace.ui.WorkspaceCreator";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public PortalCreator(DataObject dataObject) {
        super(dataObject);
    }
}
