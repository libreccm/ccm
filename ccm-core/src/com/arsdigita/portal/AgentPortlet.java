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
package com.arsdigita.portal;

import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.bebop.portal.PortletRenderer;
import com.arsdigita.domain.DataObjectNotFoundException;
// same package
// import com.arsdigita.portal.Portlet;
// import com.arsdigita.portal.Portal;
// import com.arsdigita.portal.PortletType;
import com.arsdigita.kernel.Resource;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

/**
 *
 * <p>
 * This Portlet class is used for creating portlets to be rendered
 * in a sub portal.
 * @author Jim Parsons
 */
public class AgentPortlet extends Portlet {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.portal.AgentPortlet";
    private static Logger s_log = Logger.getLogger(AgentPortlet.class);

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public AgentPortlet(DataObject dataObject) {
        super(dataObject);
    }

    public AgentPortlet(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public static AgentPortlet 
              createAgentPortlet(Portlet p, Resource parent, Portal portal) {
        PortletType ptype = 
             PortletType.retrievePortletTypeForPortlet(BASE_DATA_OBJECT_TYPE);
        Assert.exists(ptype, "PortletType");
        AgentPortlet aportlet = (AgentPortlet) Resource.createResource
                      (ptype, p.getTitle(), parent);
        aportlet.setCellNumber(p.getCellNumber());
        aportlet.setSortKey(p.getSortKey());
        aportlet.setPortal(portal);
        aportlet.setSuperPortlet(p);
        return aportlet;
 
    }


    //
    // Association properties
    //

    // To make this role accessible to the Portal domain object.
    public void setSuperPortlet(final Portlet portlet) {
        Assert.exists(portlet);

        setAssociation("superportlet", portlet);

    }

    // Cannot return null.
    public Portlet getSuperPortlet() {
        DataObject dataObject = (DataObject)get("superportlet");

        Portlet portlet = Portlet.retrievePortlet(dataObject);

        Assert.exists(portlet);

        return portlet;
    }


    //
    // Portlet rendering methods
    //

    public PortletRenderer getPortletRenderer() {
        Portlet p = getSuperPortlet();
        AbstractPortletRenderer portletRenderer = p.doGetPortletRenderer();

        portletRenderer.setTitle(getTitle());
        portletRenderer.setCellNumber(getCellNumber());
        portletRenderer.setSortKey(getSortKey());
        portletRenderer.setProfile(getProfile());

        return portletRenderer;
    }

}
