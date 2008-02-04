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
package com.arsdigita.ui.permissions;

import com.arsdigita.bebop.PageState;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;

/**
 * This class is mainly instantiated from a PageState. It is very
 * context specific for permissions. It tries to read the object_id
 * and load the corresponding ACSObject, as well as the party_id and
 * the corresponding entity.
 *
 * @author Stefan Deusch
 * @version $id$
 */
class UserObjectStruct implements PermissionsConstants {
    private static ParameterModel s_object_id_param
        = new BigDecimalParameter(OBJECT_ID);

    private Party m_party;
    private ACSObject m_object;

    UserObjectStruct(PageState s) {
        this(getParty(s), getObject(s));
    }

    UserObjectStruct(BigDecimal partyID, BigDecimal objectID){
        this(loadParty(partyID), loadObject(objectID));
    }

    UserObjectStruct(Party p, ACSObject o) {
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
        obj = (ACSObject) DomainObjectFactory
                .newInstance(new OID(ACSObject.BASE_DATA_OBJECT_TYPE, id));
        return obj;
    }

    static ACSObject loadObject(OID oid) {
        ACSObject obj = null;
        obj = (ACSObject) DomainObjectFactory.newInstance(oid);
        return obj;
    }

    // use in package

    static Party loadParty(BigDecimal id) {
        Party party = null;
        try {
            party = User.retrieve(id);
        } catch(DataObjectNotFoundException e1) {
            // ok, then try to load a group
              party = new Group(id);
        }
        return party;
    }

    public static Party getParty(PageState s) {
        return Kernel.getContext().getParty();
    }

    public static ACSObject getObject(PageState s) {
        BigDecimal id = (BigDecimal)s.getValue(s_object_id_param);
        return loadObject(id);
    }

}
