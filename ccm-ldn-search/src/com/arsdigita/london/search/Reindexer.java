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

package com.arsdigita.london.search;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.london.util.Program;
import com.arsdigita.london.util.Transaction;

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.OID;

import com.arsdigita.cms.ContentPage;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;


public class Reindexer extends Program {
    private static final Logger s_log = Logger.getLogger( Reindexer.class );

    public Reindexer() {
        super("Reindexer",
              "1.0.0",
              "");
        
        getOptions().addOption
            (OptionBuilder
             .hasArg(true)
             .withLongOpt("object-type")
             .withDescription("Restrict to objects a type")
             .create('t'));
        getOptions().addOption
            (OptionBuilder
             .hasArg(true)
             .withLongOpt("filter-query")
             .withDescription("Name of a query for filtering objects")
             .create('f'));
        getOptions().addOption
            (OptionBuilder
             .hasArg(false)
             .withLongOpt("ignore-errors")
             .withDescription("Ignore any errors")
             .create('i'));
    }

    public void doRun(CommandLine cmdLine) {
        final boolean ignoreErrors = cmdLine.hasOption("i");
        String baseType = cmdLine.getOptionValue("t");
        if (baseType == null) {
            baseType = ContentPage.BASE_DATA_OBJECT_TYPE;
        }

        String filter = cmdLine.getOptionValue("f");
        DataCollection objects = SessionManager.getSession().retrieve(baseType);
        if (filter != null) {
            objects.addInSubqueryFilter("id", filter);
        }
        
        List toProcess = new ArrayList();
        while (objects.next()) {
            toProcess.add(objects.getDataObject().getOID());
        }
        
        Iterator it = toProcess.iterator();
        int i = 1;
        while (it.hasNext()) {
            final OID oid = (OID)it.next();
            
            s_log.info("Processing " + i++ + " of " + toProcess.size() + " " + oid);
            Transaction txn = new Transaction() {
                    public void doRun() {
                        DomainObject obj = DomainObjectFactory.newInstance(oid);
                        obj.save();
                    }
                };
            try {
                txn.run();
            } catch (Throwable ex) {
                s_log.error("Cannot reindex " + oid, ex);
                if (!ignoreErrors) {
                    return;
                }
            }
        }
    }
    
    public static void main(String[] args) {
        new Reindexer().run(args);
    }
}
