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

package com.arsdigita.persistence.pdl;

import com.arsdigita.db.DbHelper;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.redhat.persistence.metadata.Table;
import com.redhat.persistence.pdl.DDLWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * TestPDLGenerator
 *
 */
public class TestPDLGenerator {

    /**
     * Compiles pdl files specifically for tests. Organizes the generation & output of SQL
     *
     * @throws PDLException if we have too few input files or if we
     * detect an error while parsing an input file. The reason we use
     * an exception is for the build process within ant to fail on
     * error.
     **/
    public static final void main(String[] args) throws PDLException {

        Map options = new HashMap();
        args = PDL.CMD.parse(options, args);

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

        List library = PDL.findPDLFiles((File[]) options.get("-library-path"));
        List files = PDL.findPDLFiles((File[]) options.get("-path"));
        files.addAll(Arrays.asList(args));

        if (files.size() < 1) {
            throw new PDLException(PDL.CMD.usage());
        }

        File debugDir = (File) options.get("-generate-events");
        if (debugDir != null) {
            if (!debugDir.exists() || !debugDir.isDirectory()) {
                throw new PDLException("No such directory: " + debugDir);
            }
            PDL.setDebugDirectory(debugDir);
        }

	PDL.compilePDLFiles(library);
	PDL.compilePDLFiles(files);

        Map map = getTestDirectoryMapping(files);
        for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
            String directory = (String) it.next();
            List filesForDirectory = (List) map.get(directory);
            generateSQL(directory, filesForDirectory, options);
        }
    }

    private static Map getTestDirectoryMapping(List files) {
        HashMap map = new HashMap();
        for (Iterator iterator = files.iterator(); iterator.hasNext();) {
            String file = (String) iterator.next();
            String directory = file.substring(0, file.lastIndexOf(File.separator));
            List dirList = (List) map.get(directory);
            if (null == dirList) {
                dirList = new LinkedList();
                map.put(directory, dirList);
            }
            dirList.add(file);
        }

        return map;

    }

    private static void generateSQL(String directory, List files, Map options)
	throws PDLException {
        MetadataRoot root = MetadataRoot.getMetadataRoot();

        String ddlDir = (String) options.get("-generate-ddl");
        if (ddlDir != null) {

            String subdir = directory.substring(directory.indexOf(File.separator + "com" + File.separator));
            ddlDir += subdir;
            Set sqlFiles = new HashSet();
            File sqldir = (File) options.get("-sqldir");
            if (sqldir != null) {
                PDL.findSQLFiles(sqldir, sqlFiles);
            }
            File file = new File(ddlDir);
            file.mkdirs();

            DDLWriter writer = new DDLWriter(ddlDir, sqlFiles);

            writer.setTestPDL(true);

            List tables = new ArrayList(root.getRoot().getTables());
            for (Iterator it = tables.iterator(); it.hasNext(); ) {
                Table table = (Table) it.next();
                if (!files.contains(root.getRoot().getFilename(table))) {
                    it.remove();
                }
            }

            try {
                writer.write(tables);
            } catch (IOException ioe) {
                throw new PDLException(ioe.getMessage());
            }
        }
    }

}
