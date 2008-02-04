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

import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Resource;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

import org.apache.log4j.Logger;

/**
 * 
 *
 * <p>A domain class for portals.  A Portal has a set of {@link
 * Portlet}s.</p>
 *
 * <p>
 * A Portal is a Persistence-backed framework for aggregating content
 * from multiple web applications/content sources. 
 * The <code>com.arsdigita.portal
 * </code> package is an infrastructural package that can be used to quickly
 * implement a basic portal server, but is primarily intended as a
 * foundation for the development of more sophisticated Portal systems such as
 * workspaces that feature multiple portals as named tabs within the workspace.
 * </p>
 * <p>
 * Containment properties of Portals, such as versioning, permissioning, and
 * searchability are available within Portal's parent class {@link Resource}.
 * </p>
 *
 * @author Justin Ross
 * @author James Parsons
 * @version $Id: Portal.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class Portal extends Resource {
    public static final String versionId = "$Id: Portal.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.portal.Portal";

    private static final Logger s_log = Logger.getLogger(Portal.class);

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    protected Portal(DataObject dataObject) {
        super(dataObject);
    }

    protected Portal(String dataObjectType) {
        super(dataObjectType);
    }

    public Portal(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Create a new portal template.
     *
     * @param title the default title of the portal template.
     * @return a new portal template.
     * @pre title != null
     * @post return != null
     */
    public static Portal createTemplate(String title) {
        Assert.exists(title, String.class);

        Portal portal = new Portal(BASE_DATA_OBJECT_TYPE);

        portal.setTitle(title);
        portal.setTemplate(true);

        return portal;
    }

    /**
     * Create a new portal.
     *
     * @param title the default title of the portal template.
     * @return a new portal template.
     * @pre title != null
     * @post return != null
     */
    public static Portal create(String title, Resource parent) {
        Assert.exists(title, String.class);

        Portal portal = 
             (Portal)Resource.createResource(BASE_DATA_OBJECT_TYPE,title,parent);

        portal.setTemplate(false);

        return portal;
    }

    public static Portal create(String dataobj, String title, Resource parent) {
        Assert.exists(title, String.class);

        Portal portal = 
             (Portal)Resource.createResource(dataobj,title,parent);

        portal.setTemplate(false);

        return portal;
    }

    /**
     * Retrieve an existing portal based on a portal ID.
     *
     * @param portalID the ID of the portal to retrieve.
     * @return an existing portal.  Note that the return value may be
     * null if no portal of this ID exists.
     * @pre portalID != null
     */
    public static Portal retrieve(BigDecimal portalID) {
        Assert.exists(portalID, BigDecimal.class);

        return Portal.retrieve(new OID(BASE_DATA_OBJECT_TYPE, portalID));
    }

    /**
     * Retrieve an existing portal based on an OID.
     *
     * @param oid the OID of the portal to retrieve.
     * @return an existing portal.  Note that the return value may be
     * null if no portal data object for this ID exists.
     * @pre oid != null
     */
    public static Portal retrieve(OID oid) {
        Assert.exists(oid, OID.class);

        DataObject dataObject = SessionManager.getSession().retrieve(oid);

        return Portal.retrieve(dataObject);
    }

    /**
     * Retrieve an existing portal based on a portal data object.
     *
     * @param dataObject the data object of the portal to retrieve.
     * @return an existing portal.  Note that the return value may be
     * null if no portal data object for this ID exists.
     * @pre dataObject != null
     */
    public static Portal retrieve(DataObject dataObject) {
        Assert.exists(dataObject, DataObject.class);

        return new Portal(dataObject);
    }

    public static PortalCollection retrieveAll() {
        DataCollection dataCollection =
            SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);

        PortalCollection portalCollection = new PortalCollection
            (dataCollection);

        return portalCollection;
    }

    /**
     * Deletes all portlets on the portal
     */
    public void clearPortlets() {
        DataAssociationCursor dac = getPortletsAssociation().cursor();
        while (dac.next()) {
            DomainObjectFactory.newInstance(dac.getDataObject()).delete();
        }
    }

    //
    // Member properties
    //

    public String getTitle() {
        String title = (String)get("title");

        Assert.exists(title, String.class);

        return title;
    }

    public void setTitle(String title) {
        Assert.exists(title, String.class);

        set("title", title);
    }

    public boolean isTemplate() {
        Boolean isTemplate = (Boolean)get("isTemplate");

        Assert.exists(isTemplate, Boolean.class);

        return isTemplate.booleanValue();
    }

    protected void setTemplate(boolean isTemplate) {
        set("isTemplate", new Boolean(isTemplate));
    }

    //
    // For portlets
    //

    // Only the methods getPortletListForCell() and save() may access
    // this map.  Otherwise, we endanger thread safety.
    private Map m_cellPortletListMap = new HashMap();

    private synchronized LinkedList getPortletListForCell(int cellNumber) {
        Integer cellNumberInteger = new Integer(cellNumber);

        if (m_cellPortletListMap.get(cellNumberInteger) == null) {
            // XXX Need to synchronize this.
            LinkedList portletList = new LinkedList();
            //(LinkedList)Collections.synchronizedList(new LinkedList());

            PortletCollection portlets = getPortletsForCell(cellNumber);

            while (portlets.next()) {
                portletList.add(portlets.getPortlet());
            }

            m_cellPortletListMap.put(cellNumberInteger, portletList);
        }

        return (LinkedList)m_cellPortletListMap.get(cellNumberInteger);
    }

    /**
     * Add a portlet to this portal or portal template.  Portlets will
     * sort in the order in which they are added to a given cell.
     *
     * @param portlet the portlet instance to add.
     * @param cellNumber the cell in which to place this portlet.  cellNumber's
     * value must be greater than or equal to 1.
     * @pre portlet != null
     * @pre cellNumber >= 1
     */
    public void addPortlet(Portlet portlet, int cellNumber) {
        Assert.exists(portlet, Portlet.class);
        Assert.truth(cellNumber >= 1, "cellNumber >= 1");

        LinkedList portletList = getPortletListForCell(cellNumber);
        synchronized (portletList) {
            portletList.add(portlet);
        }

        portlet.setCellNumber(cellNumber);
        portlet.setPortal(this);

    }

    /**
     * Return all of this Portal's Portlets, ordered by cell number
     * then sort key.
     *
     * @return a set of Portlets in a PortletCollection.
     * @post return != null
     */
    public PortletCollection getPortlets() {
        DataAssociation portlets = getPortletsAssociation();
        DataAssociationCursor portletsCursor
            = portlets.getDataAssociationCursor();
        portletsCursor.addOrder("cellNumber");
        portletsCursor.addOrder("sortKey");

        return new PortletCollection(portletsCursor);
    }

    /**
     * Return all of this Portal's Portlets for the given cell.
     *
     * @return a set of Portlets in a PortletCollection.
     * @post return != null
     */
    public PortletCollection getPortletsForCell(int cellNumber) {
        DataAssociation portlets = getPortletsAssociation();
        DataAssociationCursor portletsCursor
            = portlets.getDataAssociationCursor();

        portletsCursor.addEqualsFilter("cellNumber", new Integer(cellNumber));
        portletsCursor.addOrder("sortKey");

        return new PortletCollection(portletsCursor);
    }

    //
    // Portlet position controls
    //

    public void swapPortletWithPrevious(Portlet portlet)
        throws PersistenceException {

        LinkedList portletList = getPortletListForCell(portlet.getCellNumber());
        int currentIndex = portletList.indexOf(portlet);

        Assert.truth(currentIndex != -1, "Portlet not found.");

        try {
            synchronized (portletList) {
                portletList.remove(currentIndex);
                portletList.add(currentIndex - 1, portlet);
            }
        } catch (IndexOutOfBoundsException e) {
            // Portlets stay where they are.  Restore the removed list
            // element.
            portletList.add(currentIndex, portlet);
        }
    }

    public void swapPortletWithNext(Portlet portlet)
        throws PersistenceException {

        LinkedList portletList = getPortletListForCell(portlet.getCellNumber());
        int currentIndex = portletList.indexOf(portlet);

        Assert.truth(currentIndex != -1, "Portlet not found.");

        try {
            synchronized (portletList) {
                portletList.remove(currentIndex);
                portletList.add(currentIndex + 1, portlet);
            }
        } catch (IndexOutOfBoundsException e) {
            // Portlets stay where they are.  Restore the removed list
            // element.
            portletList.add(currentIndex, portlet);
        }
    }

    public void movePortletToHead(Portlet portlet)
        throws PersistenceException {

        LinkedList portletList = getPortletListForCell(portlet.getCellNumber());
        int currentIndex = portletList.indexOf(portlet);

        Assert.truth(currentIndex != -1, "Portlet not found.");

        synchronized (portletList) {
            portletList.remove(currentIndex);
            portletList.addFirst(portlet);
        }
    }

    public void movePortletToTail(Portlet portlet)
        throws PersistenceException {

        LinkedList portletList = getPortletListForCell(portlet.getCellNumber());
        int currentIndex = portletList.indexOf(portlet);

        Assert.truth(currentIndex != -1, "Portlet not found.");

        synchronized (portletList) {
            portletList.remove(currentIndex);
            portletList.addLast(portlet);
        }
    }



    protected void beforeSave() {
        if (s_log.isDebugEnabled()) {
            s_log.debug("In before save on portal " + this);
        }
        Iterator mapIter = m_cellPortletListMap.entrySet().iterator();

        // Generate sort keys and save contained portlets
        //
        // There will be an entry in the map when (a) a portlet has
        // been newly added or (b) an existing portlet's position in a
        // cell has changed.  In either case, we want to save the
        // updated portlet.

        while (mapIter.hasNext()) {
            Map.Entry entry = (Map.Entry)mapIter.next();

            List portletList = (List)entry.getValue();
            Iterator listIter = portletList.iterator();

            for (int sortKey = 0; listIter.hasNext(); sortKey++) {
                Portlet portlet = (Portlet)listIter.next();
                portlet.setSortKey(sortKey);
            }
        }

        super.beforeSave();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Done before save on portal " + this);
        }
    }

    //
    // Private helpers
    //

    private DataAssociation getPortletsAssociation() {
        return (DataAssociation)get("portlet");
    }
}
