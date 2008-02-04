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
package com.arsdigita.portal;

import com.arsdigita.bebop.portal.PortalModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.util.Assert;
import org.apache.log4j.Category;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.math.BigDecimal;

/**
 *
 *
 * <p>A default implementation of {@link
 * com.arsdigita.bebop.portal.PortalModel} that provides a stateful
 * backing to the Bebop portal classes.</p>
 *
 * @see com.arsdigita.bebop.portal.PortalModel
 * @see Portal
 * @see Portlet
 * @author Justin Ross
 * @author James Parsons
 * @version $Id: DefaultPortalModel.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class DefaultPortalModel implements PortalModel {
    public static final String versionId = "$Id: DefaultPortalModel.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static Category s_cat = Category.getInstance
        (DefaultPortalModel.class.getName());

    private Portal m_portal;

    /**
     * Create a new DefaultPortalModel based on the {@link Portal}
     * retrievable using portalIDParam.
     *
     * @param pageState represents the current request.
     * @param portalIDParam the parameter holding the ID for this portal.
     * @pre pageState != null
     * @pre portalIDParam != null
     */
    public DefaultPortalModel
        (PageState pageState, BigDecimalParameter portalIDParam) {

        Assert.assertNotNull(pageState);
        Assert.assertNotNull(portalIDParam);

        BigDecimal portalID = (BigDecimal)pageState.getValue
            (portalIDParam);

        m_portal = Portal.retrieve(portalID);

        Assert.assertNotNull(m_portal);
    }

    /**
     * Create a new DefaultPortalModel based on the {@link Portal}
     * retrievable via portalID.
     *
     * @param portalID the ID of the portal you'd like to retrieve.
     * @pre portalID != null
     */
    public DefaultPortalModel(BigDecimal portalID) {
        Assert.assertNotNull(portalID);

        m_portal = Portal.retrieve(portalID);

        Assert.assertNotNull(m_portal);
    }

    /**
     * Create a new DefaultPortalModel from the provided Portal.
     *
     * @param portal the portal around which to build a PortalModel.
     * @pre portal != null
     */
    public DefaultPortalModel(Portal portal) {
        Assert.assertNotNull(portal);

        m_portal = portal;
    }

    /**
     * Returns an iterator over a set of Portlets for this
     * PortalModel's Portal.
     *
     * @return an Iterator over a this Portal's Portlets.
     * @post return != null
     */
    public Iterator getPortletRenderers() {
        PortletCollection portlets = m_portal.getPortlets();
        ArrayList portletList = new ArrayList();

        while (portlets.next()) {
            Portlet portlet = portlets.getPortlet();
            //Portlet portlet = getPossiblyCachedPortlet(portlets);

            portletList.add(portlet.getPortletRenderer());
        }

        return portletList.iterator();
    }

    // Cacheing for getPortletRenderers().  This does no cache
    // eviction right now.
    //
    // NOTE: The cacheing scheme used for portlets is 'opt-out-able'
    // instead of 'opt-in-able'.  I did this on the notion that:
    //
    //   * Many portlets will access the database individually.  Or do
    //     other work individually.  Since these operations cannot be
    //     performed in aggregate, they have the potential to make a
    //     page with 15 portlets pretty slow.
    //
    //   * Many portlets will remain 'fresh' for at least a small
    //     amount of time.
    //
    //   * Portlet authors ought to be able to forget about cacheing
    //     in the common case, which I think the above two points
    //     describe.  Portlets can be made to opt out of cacheing.
    //
    // NOTE: At present we are not using this cacheing because there
    // is not a way to use data objects, and thus domain objects,
    // across transactions, or to set them read-only.  When it becomes
    // possible to do this, we will modify the code below and turn on
    // cacheing.

    // These must be accessed only in the synchronized blocks below.
    private static HashMap s_cachedPortlets = new HashMap();
    // private static HashMap s_portletExpirationDates = new HashMap();

    // This method is not currently used, so I'm commenting it out. --
    // 2002-11-26
//     private Portlet getPossiblyCachedPortlet(PortletCollection portlets) {
//         BigDecimal portletID = portlets.getID();

//         synchronized(s_cachedPortlets) {
//             synchronized(s_portletExpirationDates) {
//                 Long expirationDateLong =
//                     (Long)s_portletExpirationDates.get(portletID);

//                 long expirationDate = 0;

//                 if (expirationDateLong != null) {
//                     expirationDate = expirationDateLong.longValue();
//                 }

//                 long now = System.currentTimeMillis();

//                 boolean cacheHasPortlet = s_cachedPortlets.containsKey
//                     (portletID);

//                 if (cacheHasPortlet && now < expirationDate) {
//                     return (Portlet)s_cachedPortlets.get(portletID);
//                 }
//             }
//         }

//         // If we found something fresh in the cache, we don't get to here.

//         Portlet portlet = portlets.getPortlet();

//         synchronized(s_cachedPortlets) {
//             synchronized(s_portletExpirationDates) {
//                 s_portletExpirationDates.put
//                     (portletID, new Long(portlet.newExpirationDate()));
//                 s_cachedPortlets.put(portletID, portlet);
//             }
//         }

//         return portlet;
//     }

    /**
     * Get the title of this PortalModel's Portal.
     *
     * @return the title of this Portal.
     * @post return != null
     */
    public String getTitle() {
        return m_portal.getTitle();
    }
}
