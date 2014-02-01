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
package com.arsdigita.metadata;

import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.pdl.PDL;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;

/**
 * PersistenceTestCase
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 */

public class PersistenceTestCase extends TestCase {

    

    // Prevent loading the same PDL file twice
    private static Set s_loadedPDLResources = new HashSet();

    protected static void load(String resource) {
        if (s_loadedPDLResources.contains(resource)) {
            return;
        }
        try {
            PDL m = new PDL();
            m.loadResource(resource);
            m.generateMetadata(MetadataRoot.getMetadataRoot());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e.getMessage());
        }
        s_loadedPDLResources.add(resource);
    }


    public PersistenceTestCase(String name) {
        super(name);
    }

    /**
     * Runs the bare test sequence.
     *
     * @exception Throwable if any exception is thrown
     **/

    public void runBare() throws Throwable {
        persistenceSetUp();
        try {
            super.runBare();
        } finally {
            persistenceTearDown();
        }
    }

    protected void persistenceSetUp() {

        m_session = SessionManager.getSession();
        // Just in case, clean up any outstanding transactions
        if (m_session.getTransactionContext().inTxn()) {
            m_session.getTransactionContext().abortTxn();
        }
        m_session.getTransactionContext().beginTxn();
    }

    protected void persistenceTearDown() {
        m_session = SessionManager.getSession();
        if (m_session.getTransactionContext().inTxn()) {
            m_session.getTransactionContext().abortTxn();
        }
    }

    protected Session getSession()
    {
        return m_session;
    }

    private Session m_session;

}
