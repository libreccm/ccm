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
package com.arsdigita.cms.ui.permissions;


import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

/**
 * This class is mainly instantiated from a PageState It is very
 * context specific for permissions. It tries to read the
 * object_id and load the corresponding ACSObject, as well as
 * the party_id and the corresponding entity.
 *
 * @author Stefan Deusch (sdeusch@arsdigita.com)
 * @version $Id: CMSUserObjectStruct.java 287 2005-02-22 00:29:02Z sskracic $
 */
class CMSUserObjectStruct implements CMSPermissionsConstants {

    private static final Logger s_log = Logger.getLogger
        (CMSUserObjectStruct.class);

    private Party m_party;
    private ACSObject m_object;

    CMSUserObjectStruct(PageState s, ACSObjectSelectionModel model) {
        this(getParty(s), getObject(s, model));
    }

    CMSUserObjectStruct(BigDecimal partyID, BigDecimal objectID){
        this(loadParty(partyID), loadObject(objectID));
    }

    CMSUserObjectStruct(Party p, ACSObject o) {
        m_party  = p;
        m_object = o;
    }

    Party getParty() {
        return m_party;
    }

    ACSObject getObject() {
        return m_object;
    }

    // Utility factory methods

    static ACSObject loadObject(BigDecimal id) {
        ACSObject obj = null;
        try {
            obj = (ACSObject) DomainObjectFactory
                .newInstance(new OID(ACSObject.BASE_DATA_OBJECT_TYPE, id));
        } catch(DataObjectNotFoundException e) {
            throw new UncheckedWrapperException( (String) GlobalizationUtil.globalize("cms.ui.permissions.could_not_find_this_object").localize(), e);
        }
        return obj;
    }

    static ACSObject loadObject(OID oid) {
        ACSObject obj = null;
        try {
            obj = (ACSObject) DomainObjectFactory.newInstance(oid);
        } catch(DataObjectNotFoundException e) {
            throw new UncheckedWrapperException( (String) GlobalizationUtil.globalize("cms.ui.permissions.could_not_find_this_object").localize() + oid, e);
        }
        return obj;
    }

    // use in package

    static Party loadParty(BigDecimal id) {
        Party party = null;
        try {
            party = User.retrieve(id);
        } catch(DataObjectNotFoundException e1) {
            // ok, then try to load a group
            try {
                party = new Group(id);
            } catch(DataObjectNotFoundException e2) {
                throw new UncheckedWrapperException("No User in Database with the " +
                                           "id " + id, e2);
            }
        }
        return party;
    }

    public static Party getParty(PageState s) {
        return Kernel.getContext().getParty();
    }

    public static ACSObject getObject(PageState s, ACSObjectSelectionModel model) {
        return (ACSObject) model.getSelectedObject(s);
    }

}
