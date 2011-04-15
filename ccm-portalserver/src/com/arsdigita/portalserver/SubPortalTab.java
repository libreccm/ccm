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
package com.arsdigita.portalserver;

//import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.Session;
//import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataQueryDataCollectionAdapter;
import com.arsdigita.portal.Portal;
import com.arsdigita.portal.PortletCollection;
import com.arsdigita.kernel.Resource;
//import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.util.Assert;

import java.util.ArrayList;

/**
 * A <code>PortalTab</code> is a tool organizing content in a Portal.
 * A PortalTab includes logic for sorting and displaying portlets
 * in a specific layout manner.
 *
 * This Class extends PortalTab and is aware of a another PortalTab
 * on the system that it relies on for part of its Portlets. It also
 * overrides the getPortlets() method, and the PortletCollection
 * returned is a union of its native Portlets plus the Portlets
 * that reside in locked tiles in the associated PortalTab.
 * 
 * @author Jim Parsons
 */ 
public class SubPortalTab extends PortalTab {

    private static final int SORT_KEY_JUMP = 10;

    /**
     * The type of the {@link com.arsdigita.persistence.DataObject} that
     * stands behind this {@link com.arsdigita.domain.DomainObject}.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.workspace.SubWorkspaceTab";

    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Standard Constructor retrieves SubPortalTab from 
     * @param dataObject
     */
    public SubPortalTab(DataObject dataObject) {
        super(dataObject);
    }


   /**
    * 
    * @param title
    * @param parent
    * @return
    */
    public static SubPortalTab createSubTab(String title, Resource parent) {
        SubPortalTab ptab =
           (SubPortalTab)Portal.create(BASE_DATA_OBJECT_TYPE, title, parent);
        ptab.setSortKey(0);
        return ptab;
   }

   public static SubPortalTab createSubTab(String title) {
        SubPortalTab ptab =
           (SubPortalTab)Portal.create(BASE_DATA_OBJECT_TYPE, title, null);
        ptab.setSortKey(0);
        return ptab;
   }
  
   @Override
   public PortletCollection getPortlets() {
       PortalTab ptab = getSuperPortalTab();
       //If the super portal tab that this subportal tab is associated with
       //has been deleted, unlock the cells in this tab,
       //and then just call super.getPortlets();
       if(ptab == null) {
         String thislayout = this.getLayout();
         this.setLayout(thislayout.toUpperCase());
         return super.getPortlets();
       }

       String layout = ptab.getLayout();

       //This section builds up a list of cell numbers that must be
       //rendered from the superportal tab
       ArrayList l = new ArrayList();
       char c;
       boolean found_one = false;
       for(int i=0;i < layout.length();i++) {
           c = layout.charAt(i);
           if((c == 'n') || (c == 'w')) {
             l.add(new Integer(i));
             found_one = true;
           }
       }

       if(found_one) {    //At least one cell region was found to be locked
         Session session = SessionManager.getSession();
         DataQuery query = 
          session.retrieveQuery("com.arsdigita.workspace.getSubPortalPortlets");
         query.setParameter("subPortalTabID", this.getID());
         query.setParameter("cells", l);
         DataCollection dc = 
           new DataQueryDataCollectionAdapter(query,"portlet");
         PortletCollection pc = new PortletCollection(dc);
         return pc; 
       } else { //this case means the super tab still exists, but no longer
                //has locked regions.
         return super.getPortlets();
       }
   }   


    public void setSuperPortalTab(PortalTab ptab) {
        setAssociation("workspaceTab", ptab);
    }

    public PortalTab getSuperPortalTab() {
        DataObject dataObject = (DataObject) get("workspaceTab");

     // Assert.assertNotNull(dataObject, "dataObject");
        Assert.exists(dataObject, "dataObject");

        return new PortalTab(dataObject);
    }

}
