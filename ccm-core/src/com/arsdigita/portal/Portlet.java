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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.bebop.portal.PortletRenderer;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.Resource;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * <p>
 * A Portlet is a domain class that provides a window or <i>channel</i> into 
 * a content source (such as a web application, content item, remote content site).
 * The portlet is added, usually along with other portlets, to a {@link Portal}
 * which provides a way for multiple sources of content to be agregated
 * on one web page view.</p>
 * <p>
 * The database object which backs a Portlet contains a sortkey and a cell
 * number which are used by the containing {@link Portal} to determine
 * where on the Portal page the Portlet should appear. The Portlet's 
 * database object also includes a 'Profile' field. This field may be used
 * by implementors to set a value for a recommended width for a particular
 * portlet.</p>
 *
 * <p>
 * The contents of a Portlet are rendered via a {@link PortletRenderer}.
 * </p> 
 * <p>
 * The Portlet class was designed to be extended for custom implementations.</p>
 *
 * @author Justin Ross
 * @version $Id: Portlet.java 1549 2007-03-29 14:58:55Z chrisgilbert23 $
 */
public class Portlet extends Resource {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.portal.Portlet";
    private static Logger s_log = Logger.getLogger(Portlet.class);

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    protected Portlet(DataObject dataObject) {
        super(dataObject);
    }

    public Portlet(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    // Param parent can be null.
    public static Portlet createPortlet
        (PortletType portletType, Resource parent) {
        Assert.exists(portletType, PortletType.class);

        return (Portlet) Resource.createResource
            (portletType, portletType.getTitle(), parent);
    }

    public static Portlet createPortlet
        (String portletObjectType, Resource parent) {
        Assert.exists(portletObjectType, String.class);

        PortletType portletType =
            PortletType.retrievePortletTypeForPortlet
            (portletObjectType);

        return Portlet.createPortlet(portletType, parent);
    }

    public void beforeSave() {
        if (s_log.isDebugEnabled()) {
            s_log.debug("In before save on " + this + " " + get("sortKey"), 
                        new RuntimeException("trace"));
        }
        super.beforeSave();

        if (s_log.isDebugEnabled()) {
            s_log.debug("Done before save on " + this + " " + get("sortKey"));
        }
    }

    public void afterSave() {
        if (s_log.isDebugEnabled()) {
            s_log.debug("In after save on " + this + " " + get("sortKey"), 
                        new RuntimeException("trace"));
        }
        super.afterSave();

        // This should have already been set by Portal.beforeSave(). If it
        // hasn't this is inconsistent and is going to blow up inexplicably
        // later, so do it here instead.
        Assert.exists( get("sortKey"), Integer.class);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Middle after save on " + this + " " + get("sortKey"));
        }
        
        if (getParentResource() == null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Setting permission context for portlet " + 
                            this  + " to " + getPortal());
            }
            KernelExcursion ex = new KernelExcursion() {
                protected void excurse() {
                    setEffectiveParty(Kernel.getSystemParty());
                    PermissionService.setContext(Portlet.this, getPortal());
                }
            };

            ex.run();
        } else if (s_log.isDebugEnabled()) {
            s_log.debug("Not setting permission context");
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Done after save on " + this + " " + get("sortKey"));
        }
    }

    /**
     * Retrieve a portlet given its portlet ID.
     *
     * @return an existing portlet.  Note that if none is found, null
     * is returned.
     */
    public static Portlet retrievePortlet(BigDecimal id) {
        return (Portlet) Resource.retrieveResource(id);
    }

    /**
     * Retrieve a portlet given its OID.
     *
     * @return an existing portlet.  Note that if none is found, null
     * is returned.
     */
    public static Portlet retrievePortlet(OID oid) {
        return (Portlet) Resource.retrieveResource(oid);
    }

    /**
     * Retrieve a portlet given its data object.
     *
     * @return an existing portlet.  Note that if none is found, null
     * is returned.
     */
    public static Portlet retrievePortlet(DataObject dataObject) {
        return (Portlet) Resource.retrieveResource(dataObject);
    }

    //
    // Member properties
    //

    // profile is read only
    public String getProfile() {
        String profile = getPortletType().getProfile();
        Assert.exists(profile, String.class);
        return profile;
    }

    //
    // Association properties
    //

    // To make this role accessible to the Portal domain object.
    public void setPortal(final Portal portal) {
        Assert.exists(portal, Portal.class);

        setAssociation("portal", portal);
    }

    // Cannot return null.
    public Portal getPortal() {
        DataObject dataObject = (DataObject)get("portal");

        Portal portal = Portal.retrieve(dataObject);

        Assert.exists(portal, Portal.class);

        return portal;
    }

    public void setPortletType(PortletType portletType) {
        setResourceType(portletType);
    }

    public PortletType getPortletType() {
        DataObject dataObject = (DataObject) get("resourceType");

        dataObject.specialize(PortletType.BASE_DATA_OBJECT_TYPE);

        PortletType portletType = PortletType.retrievePortletType(dataObject);

        Assert.exists(portletType, PortletType.class);

        return portletType;
    }

    //
    // Portlet rendering methods
    //


    public void invalidateCachedPortletXML(PageState state) {
	doGetPortletRenderer().invalidateCachedVersion(state);
	
    }

    public PortletRenderer getPortletRenderer() {
        AbstractPortletRenderer portletRenderer = doGetPortletRenderer();

        portletRenderer.setTitle(getTitle());
        portletRenderer.setCellNumber(getCellNumber());
        portletRenderer.setSortKey(getSortKey());
        portletRenderer.setProfile(getProfile());

        return portletRenderer;
    }

    /**
     * Get the portlet renderer for this portlet.  The method {@link
     * #getPortletRenderer()} uses this method to return a renderer to
     * {@link DefaultPortalModel}. Subclasses of Portlet must override
     * this method and must not call <code>super()</code>.  The base
     * implementation simply throws an IllegalArgumentException.
     *
     * @see #getPortletRenderer()
     * @return this portlet's renderer.
     */
    protected AbstractPortletRenderer doGetPortletRenderer() {
        // This seems like the wrong exception to throw.
        throw new UnsupportedOperationException
            ("Portlet does not provide a default implementation"
             + " of doGetPortletRenderer, since it wouldn't be of"
             + " any use.  If you get this error, it may be because"
             + " the portal could not find the specific domain class"
             + " for a portlet using the DomainObjectFactory.");
    }

    //
    // Other
    //

    /**
     * Return the cacheing expiration date for this portlet.  {@link
     * DefaultPortalModel} uses this method to implement TTL-based
     * cacheing of portlet domain object data.  The implementation
     * provided here is the current time plus five minutes.
     *
     * @return an expiration date in the future in milliseconds.
     * @deprecated
     */
    protected long newExpirationDate() {
        // Five minutes default cacheing.
        return System.currentTimeMillis() + 1000 * 60 * 5;
    }

    // Portlets aren't really "contained" by their data source.
    protected ACSObject getContainer() {
        return null;
    }

    protected boolean isContainerModified() {
        return false;
    }

    public void setCellNumber(int cellNumber) {
        set("cellNumber", new Integer(cellNumber));
    }


    public int getCellNumber() {
        Integer cellNumber = (Integer)get("cellNumber");

        Assert.exists(cellNumber, Integer.class);

        return cellNumber.intValue();
    }

    public void setSortKey(int sortKey) {
        set("sortKey", new Integer(sortKey));
    }

    public int getSortKey() {
        Integer sortKey = (Integer)get("sortKey");

        Assert.exists(sortKey, Integer.class);

        return sortKey.intValue();
    }
}
