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

import com.arsdigita.portalserver.PortalSite;
import com.arsdigita.web.Application;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.kernel.User;
import com.arsdigita.util.Assert;

/**
 * <p><strong>Experimental</strong></p>
 *
 * @author <a href="mailto:justin@arsdigita.com">Justin Ross</a>
 * @version $Id: PersonalPortal.java  pboy $
 */
public class PersonalPortal extends PortalSite {

    public static final String BASE_DATA_OBJECT_TYPE =
                        "com.arsdigita.workspace.personal.PersonalWorkspace";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

 // protected PersonalPortal(DataObject dataObject) {
    public PersonalPortal(DataObject dataObject) {
        super(dataObject);
    }

    public static PersonalPortal createPersonalPortal
                           (User user, Application parent) {
    //  Assert.assertNotNull(user, "user");
        Assert.exists(user, "user");

        PersonalPortal portal = (PersonalPortal)
            Application.createApplication
            (BASE_DATA_OBJECT_TYPE, user.getID().toString(),
             "Personal Portal", parent);

        portal.setOwningUser(user);

        return portal;
    }

    public static PersonalPortal createPersonalPortal(User user) {
        Application parent = Application.retrieveApplicationForPath
            ("/personal-portal/");

    //  Assert.assertNotNull(parent, "parent");
        Assert.exists(parent, "parent");

        return PersonalPortal.createPersonalPortal(user, parent);
    }

    // Can return null.
    public static PersonalPortal retrievePersonalPortal(User user) {
        DataCollection portals = SessionManager.getSession().retrieve
            (BASE_DATA_OBJECT_TYPE);

        portals.addEqualsFilter("user.id", user.getID());

        PersonalPortal portal = null;

        if (portals.next()) {
            portal = (PersonalPortal) Application.retrieveApplication
                (portals.getDataObject());
        }

        portals.close();

        return portal;
    }

    private void setOwningUser(User user) {
    //  Assert.assertNotNull(user, "user");
        Assert.exists(user, "user");

        setAssociation("user", user);
    }

    public User getOwningUser() {
        DataObject dataObject = (DataObject) get("user");

     // Assert.assertNotNull(dataObject, "dataObject");
        Assert.exists(dataObject, "dataObject");

        return User.retrieve(dataObject);
    }
}
