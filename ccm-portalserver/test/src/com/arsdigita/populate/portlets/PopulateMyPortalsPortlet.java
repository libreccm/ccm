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
package com.arsdigita.populate.portlets;

import com.arsdigita.portal.PortletType;
import com.arsdigita.portal.apportlet.AppPortletType;
import com.arsdigita.portalserver.personal.MyPortalsPortlet;

/**
 * @author bche
 */
public class PopulateMyPortalsPortlet
    extends AbstractPopulatePSPortlet
    implements PopulatePortlet {

    /* (non-Javadoc)
     * @see com.arsdigita.populate.portlets.PopulatePortlet#populatePortlet()
     */
    public void populatePortlet() {
        //do nothing
    }

    /* (non-Javadoc)
     * @see com.arsdigita.populate.portlets.PopulatePortlet#getPortletType()
     */
    public PortletType getPortletType() {
        return (PortletType)AppPortletType.retrievePortletTypeForPortlet(MyPortalsPortlet.BASE_DATA_OBJECT_TYPE);
    }

}
