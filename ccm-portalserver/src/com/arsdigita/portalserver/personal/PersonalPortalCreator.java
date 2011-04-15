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
package com.arsdigita.portalserver.personal;

import com.arsdigita.web.Application;
import com.arsdigita.persistence.DataObject;

/**
 * <p><strong>Experimental</strong></p>
 *
 * This doesn't do anything right now.  We would use it if we wanted
 * to present the user with an initial configuration UI for creating
 * personal portals.
 *
 * @author <a href="mailto:justin@arsdigita.com">Justin Ross</a>
 * @version $Id: PersonalPortalCreator.java  pboy $
 */
public class PersonalPortalCreator extends Application {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.workspace.personal.PersonalWorkspaceCreator";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

//  protected PersonalPortalCreator(DataObject dataObject) {
    public PersonalPortalCreator(DataObject dataObject) {
        super(dataObject);
    }
}
