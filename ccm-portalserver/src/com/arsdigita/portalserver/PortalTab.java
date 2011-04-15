/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.portalserver;

import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.OID;
import com.arsdigita.portal.Portal;
// import com.arsdigita.portal.PortletCollection;
import com.arsdigita.kernel.Resource;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

/**
 * A <code>PortalTab</code> is a tool organizing content in a Portal.
 * A PortalTab includes logic for sorting and displaying portlets
 * in a specific layout manner.
 *
 * @author Jim Parsons
 */ 
public class PortalTab extends Portal {

    private static final int SORT_KEY_JUMP = 10;
    private static final String DEFAULT_LAYOUT = "NW";

    /**
     * The type of the {@link com.arsdigita.persistence.DataObject}
     * that stands behind this {@link
     * com.arsdigita.domain.DomainObject}.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.workspace.WorkspaceTab";

    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public PortalTab(DataObject dataObject) {
        super(dataObject);
    }


   public static PortalTab createTab(String title, Resource parent) {
        PortalTab ptab =
           (PortalTab)Portal.create(BASE_DATA_OBJECT_TYPE, title, parent);
        ptab.setSortKey(0);
        ptab.setLayout(DEFAULT_LAYOUT);
        return ptab;
   }

   public static PortalTab createTab(String title) {
        PortalTab ptab =
           (PortalTab)Portal.create(BASE_DATA_OBJECT_TYPE, title, null);
        ptab.setSortKey(0);
        ptab.setLayout(DEFAULT_LAYOUT);
        return ptab;
   }

    /**
     * Retrieve an existing PortalTab based on a PortalTab ID.
     *
     * @param portalID the ID of the PortalTab to retrieve.
     * @return an existing PortalTab.  Note that the return value may be
     * null if no PortalTab of this ID exists.
     * @pre prtlTabID != null
     */
    public static PortalTab retrieveTab(BigDecimal prtlTabID) {
     // Assert.assertNotNull(prtlTabID);
        Assert.exists(prtlTabID);

        return PortalTab.retrieveTab(new OID(BASE_DATA_OBJECT_TYPE, prtlTabID));
    }

    /**
     * Retrieve an existing PortalTab based on a portal_tab data object.
     *
     * @param dataObject the data object of the PortalTab to retrieve.
     * @return an existing PortalTab.  Note that the return value may be
     * null if no PortalTab data object for this ID exists.
     * @pre dataObject != null
     */
    public static PortalTab retrieveTab(DataObject dataObject) {
     // Assert.assertNotNull(dataObject);
        Assert.exists(dataObject);

        DomainObject dobj = DomainObjectFactory.newInstance(dataObject);
        return (PortalTab)dobj;
    }

    /**
     * Retrieve an existing PortalTab based on an OID.
     *
     * @param oid the OID of the PortalTab to retrieve.
     * @pre oid != null
     */
    public static PortalTab retrieveTab(OID oid) {
     // Assert.assertNotNull(oid);
        Assert.exists(oid);

        DataObject dataObject = SessionManager.getSession().retrieve(oid);

        return PortalTab.retrieveTab(dataObject);
    }

    public static boolean doesTabExist(BigDecimal bd) {

        OID oid = new OID(BASE_DATA_OBJECT_TYPE, bd);

        DataObject dataObject = SessionManager.getSession().retrieve(oid);

        if (dataObject == null)
            return false;
        else
            return true;
    }

    /**
     * This is an unusual method that retrieves ALL Portal Tabs...
     */
    public static PortalTabCollection retrieveAllTabs() {
        DataCollection dataCollection =
            SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);

        PortalTabCollection tabCollection = new PortalTabCollection
            (dataCollection);

        return tabCollection;
    }

    public static PortalTabCollection getTabsForPortalSite(PortalSite p) {
        DataCollection dataCollection =
            SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);

        PortalTabCollection tabCollection = new PortalTabCollection
            (dataCollection);

        return tabCollection;
    }

    //
    // Accessors
    //

    /**
     * Get the title of this PortalTab.
     *
     * @return this tab's title.
     * @post return != null
     */
    @Override
    public String getTitle() {
        String title = (String)get("title");

     // Assert.assertNotNull(title);
        Assert.exists(title);

        return title;
    }

    /**
     * Set the title of this PortalTab.
     *
     * @param title the new title.
     * @pre title != null
     */
    @Override
    public void setTitle(String title) {
     // Assert.assertNotNull(title);
        Assert.exists(title);

        set("title", title);
    }

    public void setPortalSite(PortalSite psite) {
        setAssociation("workspace", psite);
    }

    public PortalSite getPortalSite() {

        DataObject dataObject = (DataObject) get("workspace");

     // Assert.assertNotNull(dataObject, "dataObject");
        Assert.exists(dataObject, "dataObject");

        return new PortalSite(dataObject);

    }


    /**
     * Get the sort key of this PortalTab.  The sort key is used
     * to order a set of tabs in a particular Portal Site.
     *
     * @return the portal sort key.
     */
    public int getSortKey() {
        return ((Integer)get("sortKey")).intValue();
    }

    public void setSortKey(int sortKey) {
        set("sortKey", new Integer(sortKey));
    }

    //Layout getter and setter...

    public String getLayout() {
        return ((String)get("tab_layout"));
    }

    public void setLayout(String layout) {
        set("tab_layout", layout);
    }

    private boolean m_wasNew;

    @Override
    protected void beforeSave() {
        m_wasNew = isNew();
        super.beforeSave();
    }

    @Override
    public void afterSave() {
        super.afterSave();

        if (m_wasNew) {
            PortalSite psite = (PortalSite) DomainObjectFactory.newInstance(
                                  (DataObject) get("workspace"));
            PermissionService.setContext(this, psite);
        }
    }
}
