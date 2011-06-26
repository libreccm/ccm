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

import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.pdl.PDLCompiler;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.RuntimeConfig;

import java.io.StringReader;
import org.apache.log4j.Logger;

import org.apache.log4j.Level;

/**
 * Initializer loads UDCTs.
 *
 * @author Archit Shah
 * @version $Revision: #23 $ $Date: 2004/08/16 $
 **/

public class Initializer extends com.arsdigita.runtime.GenericInitializer {

    private static final Logger s_log =
        Logger.getLogger(Initializer.class.getName());

    /**
     * Sets up the session and loads the persistence metadata from a file
     * somewhere in your classpath. The name of the file to which to search for
     * is defined by the metadataXmlFileName initialization parameter.
     **/
    public void init(DomainInitEvent evt) {
        com.redhat.persistence.oql.Query.setQueryCacheSize(
                                RuntimeConfig.getConfig().getQueryCacheSize() );

        // Finally the files out of the database
        TransactionContext txn = null;
        try {            
            Session session = SessionManager.getSession();
            txn = session.getTransactionContext();
            txn.beginTxn();

            MetadataRoot root = MetadataRoot.getMetadataRoot();
            PDLCompiler pdl = new PDLCompiler();
            DataCollection collection = SessionManager.getSession()
                .retrieve("com.arsdigita.persistence.DynamicObjectType");
            while (collection.next()) {
                String currentFile = (String)collection.get("dynamicType");
                if (s_log.isInfoEnabled()) {
                    s_log.info("loading... " + currentFile.toString());
                }

                if (root.getObjectType(currentFile) != null) {
                    // this means that there is a type in the database
                    // that has already been defined so we write an error
                    if (s_log.isEnabledFor(Level.WARN)) {
                        s_log.warn
                            ("The Object Type [" + currentFile + "] has already " +
                             "been defined in the static files.  Ignoring " +
                             "object type definition from the database");
                    }
                    continue;
                }

                String pdlFile = (String)collection.get("pdlFile");
                pdl.parse(new StringReader(pdlFile),
                          "DATABASE: " + currentFile);
            }

            collection = SessionManager.getSession()
                .retrieve("com.arsdigita.persistence.DynamicAssociation");

            while (collection.next()) {
                String currentFile = "Association from " +
                    collection.get("objectType1") + " to " +
                    collection.get("objectType2");

                // XXX: I'm going to ignore the possibility of this error for
                // now.
                //s_log.warn("An association from " + currentFile + " has " +
                //"already been defined.  Ignoring version in " +
                //"the database");

                if (s_log.isInfoEnabled()) {
                    s_log.info("loading " + currentFile);
                }

                String pdlFile = (String)collection.get("pdlFile");
                pdl.parse(new StringReader(pdlFile),
                          "DATABASE: " + currentFile);
            }

            pdl.emit(root);

            //try {
            // Future use -- Patrick
            //PDLOutputter.writePDL(root, new java.io.File("/tmp/pdl"));
            //} catch (java.io.IOException e) {
            //System.out.println(e.getMessage());
            //}

            txn.commitTxn();
        } catch(Exception ex) {
            //jensp 2011-06-24: There was no catch Block for this try. This is
            //an extremly BAD practicse!!! At least there should be a log output
            //of the execption of debugging.
            s_log.error("An exception occured in the init(DomainInitEvent) method: ", ex);
        } finally {
            if (txn != null && txn.inTxn()) {
                txn.abortTxn();
            }
        }
    }

}
