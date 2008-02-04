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

package com.arsdigita.london.util.db;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.util.Assert;
import com.redhat.persistence.metadata.Column;
import com.redhat.persistence.metadata.ObjectMap;
import com.redhat.persistence.metadata.Table;
import com.redhat.persistence.metadata.UniqueKey;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import com.arsdigita.london.util.Program;
import com.arsdigita.london.util.Transaction;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import java.util.Iterator;

public class ACSObjectInvariantCheck extends Program {

    private static final Logger s_log = 
        Logger.getLogger(ACSObjectInvariantCheck.class);
    
    private static final String EXPECTED = "0";

    public ACSObjectInvariantCheck() {
        super("Bulk Publish",
              "1.0.0",
              "");
    }
    
    protected void doRun(CommandLine cmdLine) {
        final String[] args = cmdLine.getArgs();
        
        Transaction txn = new Transaction() {
                public void doRun() {
                    Iterator invariants = generateInvariants();
                    while (invariants.hasNext()) {
                        Invariant inv = (Invariant)invariants.next();
                        if (s_log.isInfoEnabled()) {
                            s_log.info("Check: " + inv.getDescription());
                        }
                        try {
                            inv.check();
                        } catch (InvariantViolationException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            };
        txn.run();
    }

    public static void main(String[] args) {
        new ACSObjectInvariantCheck().run(args);
    }

    private static Iterator generateInvariants() {
        List invariants = new ArrayList();
        MetadataRoot root = SessionManager.getSession().getMetadataRoot();

        ObjectType acsObject = root.getObjectType(ACSObject.BASE_DATA_OBJECT_TYPE);

        Iterator types = root.getObjectTypes().iterator();
        while (types.hasNext()) {
            ObjectType specificType = (ObjectType)types.next();
            
            if (!specificType.isSubtypeOf(acsObject)) {
                continue;
            }
            
            ObjectType type = specificType;
            do {
                Invariant inv = generateRowTest(specificType, type); 
                if (inv != null) {
                    invariants.add(inv);
                }
                type = type.getSupertype();
            } while (type != null);
        }
        
        return invariants.iterator();
    }

    private static Invariant generateRowTest(ObjectType specificType,
                                             ObjectType type) {
        MetadataRoot root = SessionManager.getSession().getMetadataRoot();

        com.redhat.persistence.metadata.ObjectType protoType =
            root.getRoot().getObjectType(type.getQualifiedName());
        Assert.truth(protoType != null,
                     "null proto type for " + type.getQualifiedName());

        final ObjectMap objectMap = root.getRoot().getObjectMap(protoType);
        final Table mdTable = objectMap.getTable();
        if (mdTable == null) {
            return null;
        }
        final UniqueKey primaryKey = mdTable.getPrimaryKey();
        final Column[] columns = primaryKey.getColumns();
        if (columns.length > 1) {
            throw new RuntimeException("ACSObject has > 1 column in primary key");
        }

        String col = columns[0].getName();
        String table = columns[0].getTable().getName();

        return InvariantFactory.getInstance().createInvariant(
            "Check for " + specificType.getQualifiedName() + " in " + table,
            "select count(*) as result\n" + 
            "  from acs_objects \n" +
            " where object_type = '" + specificType.getQualifiedName() + "'\n" +
            "   and not exists (\n" +
            "       select 1 \n" +
            "         from " + table + "\n" +
            "        where " + table + "." + col + " = acs_objects.object_id\n" +
            "       )",
            EXPECTED);
    }
    
}
