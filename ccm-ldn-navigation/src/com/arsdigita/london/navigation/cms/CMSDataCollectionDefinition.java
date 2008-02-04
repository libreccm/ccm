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
 */

package com.arsdigita.london.navigation.cms;


import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.london.navigation.DataCollectionDefinition;
import com.arsdigita.london.navigation.NavigationModel;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;

import com.arsdigita.persistence.metadata.ObjectType;

import com.arsdigita.util.Assert;


public class CMSDataCollectionDefinition extends DataCollectionDefinition {

    private boolean m_filterSection = false;
    private String m_filterVersion = ContentItem.LIVE;

    protected void validateObjectType(ObjectType type) {
        Assert.truth(type.isSubtypeOf(ContentItem.BASE_DATA_OBJECT_TYPE),
                     "object type is a content item");
    }

    public final void setFilterSection(boolean filterSection) {
        Assert.unlocked(this);
        m_filterSection = filterSection;
    }
    
    public final void setFilterVersion(String version) {
        Assert.unlocked(this);
        m_filterVersion = version;
    }

    protected void applyFilters(DataCollection objects,
                                NavigationModel model) {
        super.applyFilters(objects, model);

        if (m_filterSection && CMS.getContext().hasContentSection()) {
            Filter inContentSection = objects.addInSubqueryFilter(
                ACSObject.ID,
                "com.arsdigita.cms.getItemIDsInContentSection"
            );
            inContentSection.set(
                "sectionID",
                CMS.getContext().getContentSection()
            );
        }
        
        if (m_filterVersion != null) {
            objects.addEqualsFilter(ContentItem.VERSION, 
                                    m_filterVersion);
        }
        
        objects.addPath("masterVersion.id");

        // Can remove once bz 104102 is fixed
        objects.addPath("masterVersion.objectType");
    }

    protected void checkPermissions(DataCollection objects) {
        // parties are assigned the cms_read_item privilege on content items
        // rather than the primitive READ
        Party party = Kernel.getContext().getParty();
        if (party == null) {
            party = Kernel.getPublicUser();
        }

        PermissionService.filterObjects(
            objects,
            PrivilegeDescriptor.get(SecurityManager.CMS_READ_ITEM),
            party.getOID());
    }

    protected String getCategorizedObjectPath(String fragment) {
        return "parent." + fragment;
    }
}
