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
package com.arsdigita.portal.apportlet;

import com.arsdigita.portal.PortletType;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;
import java.math.BigDecimal;


/**
 *
 * @author <a href="mailto:jparsons@redhat.com">Jim Parsons</a>
 */
public final class AppPortletType extends PortletType {

    private static final Logger s_cat =
        Logger.getLogger(AppPortletType.class.getName());

    private final static String IS_PORTAL_APPLICATION =
        "isWorkspaceApplication";

    protected AppPortletType(DataObject dataObject) {
        super(dataObject);
    }

    // Create from packageType.
    protected AppPortletType
        (String dataObjectType, String title, String profile,
         String portletObjectType) {
        super(dataObjectType,title,profile,portletObjectType);

    }


    public static AppPortletType retrieveAppPortletType(BigDecimal id) {
        Assert.assertNotNull(id, "id");

        return AppPortletType.retrieveAppPortletType
            (new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public static AppPortletType retrieveAppPortletType(OID oid) {
        Assert.assertNotNull(oid, "oid");

        DataObject dataObject = SessionManager.getSession().retrieve(oid);

        Assert.assertNotNull(dataObject);

        return AppPortletType.retrieveAppPortletType(dataObject);
    }

    public static AppPortletType retrieveAppPortletType(DataObject dataObject) {
        Assert.assertNotNull(dataObject, "dataObject");

        return new AppPortletType(dataObject);
    }

    public static AppPortletType retrieveAppPortletTypeForAppPortlet
        (String portletObjectType) {
        Assert.assertNotNull(portletObjectType, "portletObjectType");

        DataCollection collection =
            SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);

        Assert.assertNotNull(collection, "collection");

        collection.addEqualsFilter("objectType", portletObjectType);

        AppPortletType portletType = null;

        if (collection.next()) {
            portletType = AppPortletType.retrieveAppPortletType
                (collection.getDataObject());
        } else {
            s_cat.warn("No portlet type found that matches \"" +
                       portletObjectType + ".\"  Check that the portlet " +
                       "type is registered in the system.");
        }

        collection.close();
        return portletType;
    }

    public static AppPortletType createAppPortletType
        (String title, String profile, String portletObjectType) {
        return new AppPortletType
            (BASE_DATA_OBJECT_TYPE, title, profile, portletObjectType);
    }


    public void setPortalApplication(boolean isPortalApplication) {
        set(IS_PORTAL_APPLICATION, new Boolean(isPortalApplication));
    }

    public boolean isPortalApplication() {
        Boolean isPortalApplication =
            (Boolean) get(IS_PORTAL_APPLICATION);

        Assert.assertNotNull(isPortalApplication, IS_PORTAL_APPLICATION);

        return isPortalApplication.booleanValue();
    }

}
