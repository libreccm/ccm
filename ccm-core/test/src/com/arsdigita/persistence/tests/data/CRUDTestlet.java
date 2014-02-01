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
package com.arsdigita.persistence.tests.data;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

/**
 * CRUDTestlet
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/

public class CRUDTestlet extends Testlet {

    

    private static final Logger LOG = Logger.getLogger(CRUDTestlet.class);

    private ObjectType m_type;

    private DataSource m_initial = new DataSource("initial values");
    private DataSource m_updated = new DataSource("updated values");

    public CRUDTestlet(String type) {
        this(MetadataRoot.getMetadataRoot().getObjectType(type));
    }

    public CRUDTestlet(ObjectType type) {
        m_type = type;
    }

    public void run() {
        ObjectTree tree =
            makeTree(m_type, ATTRIBUTE | ROLE, COLLECTION, 1);
        ObjectTree updated =
            makeTree(m_type, ATTRIBUTE | ROLE, COLLECTION | KEY, 1);
        LOG.warn("Tree: " + tree);
        LOG.warn("Updated: " + updated);

        Session ssn = SessionManager.getSession();

        // Create
        DataObject data = create(tree, m_initial);
        OID oid = data.getOID();
        data.save();

        // Retrieve and verify
        verify(ssn.retrieve(oid), tree, m_initial);

        // Update
        data = ssn.retrieve(oid);
        update(data, updated, m_updated);
        data.save();

        // Retrieve and verify
        verify(ssn.retrieve(oid), updated, m_updated);

        // Delete
        // XXX: This doesn't delete the entire tree.
        data = ssn.retrieve(oid);
        data.delete();

        // Verify that it is deleted
        Assert.assertEquals(null, ssn.retrieve(oid));
    }

}
