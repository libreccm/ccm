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
package com.arsdigita.versioning;

import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;

import java.math.BigInteger;

import org.apache.log4j.Logger;

// new versioning

/**
 * A collection of changes previously made to a data object.
 *
 * @see Versions
 * @author Stanislav Freidin
 * @author Vadim Nasardinov (vadimn@redhat.com)
 */
final class ObjectChangeCollection implements Constants {
    private final static Logger s_log =
        Logger.getLogger(ObjectChangeCollection.class);

    private DataCollection m_dc;

    /**
     * @param versionedOID the oid of the versioned data object in whose change
     * history we are interested.
     * @pre versionedOID != null
     **/
    public ObjectChangeCollection(OID versionedOID, BigInteger backTo) {
        m_dc = SessionManager.getSession().retrieve(CHANGE_DATA_TYPE);
        m_dc.addFilter(OBJ_ID + " = :bindOID").
            set("bindOID", Adapter.serialize(versionedOID));

        final String sortKey = TXN + "." + ID;
        if (backTo != null) {
            m_dc.addFilter(sortKey + " > :backTo").set("backTo", backTo);
        }
        m_dc.addOrder(sortKey + " desc");
        m_dc.addOrder(ID + " desc");
    }

    public boolean next() {
        return m_dc.next();
    }

    public void close() {
        m_dc.close();
    }

    DataCollection getOperations() {
        DataAssociation da =
            (DataAssociation) m_dc.getDataObject().get(OPERATIONS);
        DataCollection result = da.cursor();
        result.addOrder(ID + " desc");
        return result;
    }
}
