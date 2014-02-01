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
package com.arsdigita.persistence.tests.data;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import org.apache.log4j.Logger;

public class DoubleUpdateTestlet extends Testlet {

    

    private static final Logger s_log = Logger.getLogger(DoubleUpdateTestlet.class);

    private DataSource m_initial = new DataSource("initial values");
    private DataSource m_updated = new DataSource("updated values");

    private ObjectType m_type;
    private String[] m_path;

    public DoubleUpdateTestlet(String type, String[] path) {
        this(MetadataRoot.getMetadataRoot().getObjectType(type), path);
    }

    public DoubleUpdateTestlet(ObjectType type, String[] path) {
        m_type = type;
        m_path = path;
    }

    public void run() {
        ObjectTree tree = makeTree(m_type, ATTRIBUTE | ROLE, COLLECTION, 1);
        ObjectTree updated = new ObjectTree(m_type);
        updated.addPath(m_path);
        addPaths(updated.getSubtree(m_path[0]), ATTRIBUTE | ROLE, COLLECTION,
                 0);

        // Create
        DataObject data = create(tree, m_initial);
        data.save();

        // Update
        update(data, updated, m_updated);
        data.save();
        update(data, updated, m_updated);
        data.save();

        // Retrieve and verify
        verify(data.getSession().retrieve(data.getOID()), updated, m_updated);
    }
}
