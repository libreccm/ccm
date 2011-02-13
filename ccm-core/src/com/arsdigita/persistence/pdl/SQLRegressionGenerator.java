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
package com.arsdigita.persistence.pdl;

import com.arsdigita.db.DbHelper;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.cmd.BooleanSwitch;
import com.arsdigita.util.cmd.CommandLine;
import com.arsdigita.util.cmd.PathSwitch;
import com.arsdigita.util.cmd.StringSwitch;
import com.redhat.persistence.metadata.Column;
import com.redhat.persistence.metadata.ObjectMap;
import com.redhat.persistence.metadata.Table;
import com.redhat.persistence.metadata.UniqueKey;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 * SQLRegressionGenerator
 *
 * Usage:
 * <pre>
 *  cdweb
 *  cd webapps/ccm/WEB-INF
 *  java -classpath classes:lib/log4j.jar:lib/jakarta-oro-2.0.4.jar \
 *       com.arsdigita.persistence.pdl.SQLRegressionGenerator \
 *       -path pdl \
 *       -check check-objects.sql
 *       -fix fix-objects.sql
 * </pre>
 * Then to check all objects:
 * <pre>
 *  psql -h dbhost dbname < check-objects.sql
 * </pre>
 * Or to fix all (ie delete broken) objects:
 * <pre>
 *  psql -h dbhost dbname < fix-objects.sql
 * </pre>
 */
public class SQLRegressionGenerator {

    private static final Logger logger = Logger.getLogger(SQLRegressionGenerator.class);
    static final CommandLine CMD =
        new CommandLine(PDL.class.getName(), null);

    static {
        logger.debug("Static initalizer starting...");
        CMD.addSwitch(new PathSwitch(
            "-path",
            "PDL files appearing in this path will be processed",
            new File[0]
            ));
        CMD.addSwitch(new StringSwitch("-check",
                                       "generate sql for checking objects and write " +
                                       "it to the specified directory", null));
        CMD.addSwitch(new StringSwitch("-fix",
                                       "generate sql for fixing objects and write " +
                                       "it to the specified directory", null));
        CMD.addSwitch(new BooleanSwitch("-debug", "sets logging to DEBUG",
                                        Boolean.FALSE));
        CMD.addSwitch(new BooleanSwitch("-verbose", "sets logging to INFO",
                                        Boolean.FALSE));
        CMD.addSwitch(new BooleanSwitch("-quiet", "sets logging to ERROR and does not complain if no PDL files are found",
                                        Boolean.FALSE));
        CMD.addSwitch(new StringSwitch("-database", "target database", null));
        logger.debug("Static initalizer finished.");
    }

    /**
     * Generates SQL regression tests to verify objects are complete
     *
     * @throws PDLException if we have too few input files or if we
     * detect an error while parsing an input file. The reason we use
     * an exception is for the build process within ant to fail on
     * error.
     **/
    public static final void main(String[] args) throws PDLException {

        org.apache.log4j.BasicConfigurator.configure();

        Map options = new HashMap();
        args = CMD.parse(options, args);

        BasicConfigurator.configure();
        if (Boolean.TRUE.equals(options.get("-debug"))) {
            Logger.getRootLogger().setLevel(Level.DEBUG);
        } else if (Boolean.TRUE.equals(options.get("-verbose"))) {
            Logger.getRootLogger().setLevel(Level.INFO);
        } else if (Boolean.TRUE.equals(options.get("-quiet"))) {
            Logger.getRootLogger().setLevel(Level.ERROR);
        } else {
            Logger.getRootLogger().setLevel(Level.FATAL);
        }

        String database = (String) options.get("-database");
        if ("postgres".equalsIgnoreCase(database)) {
            DbHelper.setDatabase(DbHelper.DB_POSTGRES);
        } else {
            DbHelper.setDatabase(DbHelper.DB_ORACLE);
        }

        List files = PDL.findPDLFiles((File[]) options.get("-path"));
        files.addAll(Arrays.asList(args));

        if (files.size() < 1) {
            throw new PDLException(PDL.CMD.usage());
        }


        Set all = new HashSet();
        all.addAll(files);

        //MetadataRoot.clear();
        PDL.compilePDLFiles(all);

        generateSQL(files, options);
    }

    private static void generateSQL(List files, Map options) throws PDLException {
        MetadataRoot root = SessionManager.getSession().getMetadataRoot();

        ObjectType acsObject = root.getObjectType(ACSObject.BASE_DATA_OBJECT_TYPE);

        String checkFile = (String) options.get("-check");
        if (checkFile != null) {
            FileOutputStream file = null;
            try {
                file = new FileOutputStream(checkFile);
            } catch (FileNotFoundException ex) {
                throw new UncheckedWrapperException("cannot find file " + checkFile, ex);
            }
            PrintStream out = new PrintStream(file);

            Iterator types = root.getObjectTypes().iterator();
            while (types.hasNext()) {
                ObjectType specificType = (ObjectType)types.next();

                if (!specificType.isSubtypeOf(acsObject)) {
                    continue;
                }

                ObjectType type = specificType;
                do {
                    generateRowTest(specificType, type, out, false);
                    type = type.getSupertype();
                } while (type != null);
            }
        }


        String fixFile = (String) options.get("-fix");
        if (fixFile != null) {
            FileOutputStream file = null;
            try {
                file = new FileOutputStream(fixFile);
            } catch (FileNotFoundException ex) {
                throw new UncheckedWrapperException("cannot find file " + fixFile, ex);
            }
            PrintStream out = new PrintStream(file);

            Iterator types = root.getObjectTypes().iterator();
            while (types.hasNext()) {
                ObjectType specificType = (ObjectType)types.next();

                if (!specificType.isSubtypeOf(acsObject)) {
                    continue;
                }

                generateRowTest(specificType, specificType, out, true);
            }
        }
    }

    private static void generateRowTest(ObjectType specificType,
                                        ObjectType type,
                                        PrintStream out,
                                        boolean fix) {
        MetadataRoot root = SessionManager.getSession().getMetadataRoot();

        com.redhat.persistence.metadata.ObjectType protoType =
            root.getRoot().getObjectType(type.getQualifiedName());
        Assert.isTrue(protoType != null,
                     "null proto type for " + type.getQualifiedName());

        Column key;
        try {
            final ObjectMap objectMap = root.getRoot().getObjectMap(protoType);
            final Table mdTable = objectMap.getTable();
            final UniqueKey primaryKey = mdTable.getPrimaryKey();
            final Column[] columns = primaryKey.getColumns();
            key = columns[0];

        } catch(NullPointerException e) {
           System.err.println("NPE for " + type.getQualifiedName());
            return;
        }

        //Column key = type.getReferenceKey();
        if (key == null) {
            return;
        }
        String col = key.getName();
        String table = key.getTable().getName();


        if (fix) {
            out.println("select 'Deleting objects " + specificType.getQualifiedName() +
                        " not present in " + table + "." + col + "' as check from dual;\n");
            out.println("delete from acs_objects \n" +
                        " where object_type = '" + specificType.getQualifiedName() + "'\n" +
                        "   and not exists (\n" +
                        "       select 1 \n" +
                        "         from " + table + "\n" +
                        "        where " + table + "." + col + " = acs_objects.object_id\n" +
                        "       );\n\n\n");
        } else {
            out.println("select 'Verifying presence of " + specificType.getQualifiedName() +
                        " objects in " + table + "." + col + "' as chk from dual;\n");
            out.println("select object_id, display_name\n" +
                        "  from acs_objects \n" +
                        " where object_type = '" + specificType.getQualifiedName() + "'\n" +
                        "   and not exists (\n" +
                        "       select 1 \n" +
                        "         from " + table + "\n" +
                        "        where " + table + "." + col + " = acs_objects.object_id\n" +
                        "       );\n\n\n");
        }
    }

}
