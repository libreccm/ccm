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
package com.arsdigita.persistence;

import com.arsdigita.db.DbHelper;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.pdl.PDL;
import com.arsdigita.persistence.pdl.PDLOutputter;
import com.redhat.persistence.engine.rdbms.RDBMSEngine;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

/**
 * PersistenceTestCase
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #16 $ $Date: 2004/08/16 $
 */

public class PersistenceTestCase extends TestCase {

    public final static String versionId = "$Id: PersistenceTestCase.java 750 2005-09-02 12:38:44Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger LOG =
        Logger.getLogger(PersistenceTestCase.class);

    // Prevent loading the same PDL file twice
    private static Set s_loadedPDLResources = new HashSet();

    /**
     *  This loads the passed in resource.  It also checks for the existence
     *  of files with the same name for the specific database that is being
     *  used.  It does that by locating the substring "testpdl" and
     *  replacing it with "testpdl/<database-here>" such as
     *  "testpdl/oracle-se"
     *  @param outputPDL If true and if the value of "outputPDLDir" within
     *  the source code itself has been changed then it will output
     *  the PDL.  This should eventaully be fixed to take in a config
     *  file parameter so that the source code does not need to change.
     */
    protected static void load(String resource, boolean outputPDL) {
        if (s_loadedPDLResources.contains(resource)) {
            return;
        }

        s_loadedPDLResources.add(resource);

        String shadow = resource.substring(0, resource.lastIndexOf('.'));
        String ext = resource.substring(resource.lastIndexOf('.') + 1,
                                        resource.length());
        shadow = shadow + "." + DbHelper.getDatabaseSuffix() + "." + ext;

        try {
            PDL m = new PDL();

            String[] resources = new String[] {shadow, resource};
            for (int i = 0; i < resources.length; i++) {
                if (m.getClass().getClassLoader().getResourceAsStream
                    (resources[i]) != null) {
                    m.loadResource(resources[i]);
                    s_loadedPDLResources.add(resources[i]);
                    break;
                }
            }

            m.generateMetadata(MetadataRoot.getMetadataRoot());

            String outputPDLDir = null;
            if (outputPDL) {
                try {
                    PDLOutputter.writePDL(MetadataRoot.getMetadataRoot(),
                                          new java.io.File(outputPDLDir));
                } catch (java.io.IOException e) {
                    System.err.println
                        ("There was a problem generating debugging output");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e.getMessage());
        }
        s_loadedPDLResources.add(resource);
    }


    /**
     *  This loads the passed in resource.  It also checks for the existence
     *  of files with the same name for the specific database that is being
     *  used.  It does that by locating the substring "testpdl" and
     *  replacing it with "testpdl/<database-here>" such as
     *  "testpdl/oracle-se"
     */
    protected static void load(String resource) {
        load(resource, false);
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
            m_session.flushAll();
        } finally {
            persistenceTearDown();
        }
    }

    protected void persistenceSetUp() {
        LOG.warn("Starting " + getClass().getName() + "." + getName());
        m_session = SessionManager.getSession();
        m_session.getTransactionContext().beginTxn();
    }

    protected void persistenceTearDown() {
        try {
            if (m_session.getTransactionContext().inTxn()) {
                m_session.getTransactionContext().abortTxn();
            }
        } finally {
            LOG.warn("Ending " + getClass().getName() + "." + getName());
        }
    }

    protected Session getSession() {
        return m_session;
    }

    protected RDBMSEngine getEngine() {
        return m_session.getEngine();
    }
    private Session m_session;

}
