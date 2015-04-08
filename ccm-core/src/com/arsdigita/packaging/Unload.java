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
package com.arsdigita.packaging;

import com.arsdigita.runtime.CCMResourceManager;
import com.arsdigita.util.Files;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import org.apache.log4j.Logger;

/**
 * PackageTool worker class, implements the "load" command.
 *  
 * It is called by class MasterTols and unloads the database schema and initial 
 * content.
 * 
 * MasterTool provides the following parameters (usually provided by an
 * invokation script 'ccm')
 * 
 * ccm unload   PACKAGE-KEYS    [options]
 * PACKAGE-KEYS one or more space separated names of modules (package-key, e.g.
 *              ccm-cms-types-event) which should be loaded into database and
 *              configuration registry
 * Options:     [--config]  Removes entries in the registry (configuration repo)
 *                          if set prevents any of the three data load steps
 *                          described before to be executed!
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt; tosmers;
 * @version $Revision: #7 $ $Date: 2015/03/29 $
 * @version $Id: Unload.java 736 2015-03-29 14:22:20Z tosmers $
 */
class Unload extends Command {

    private static final Logger logger = Logger.getLogger(Unload.class);

    private static final Options OPTIONS = new Options();
    static {
        logger.debug("Static initalizer starting...");
        OPTIONS.addOption
            (OptionBuilder
             .hasArg(false)
             .withLongOpt("config")
             .withDescription("Unload configuration")
             .create());
        logger.debug("Static initalizer finished.");
    }

    private static final Set EXCLUDE = new HashSet();
    static {
        logger.debug("Static initalizer starting...");
        EXCLUDE.add("resin.conf");
        EXCLUDE.add("resin.pid");
        EXCLUDE.add("server.xml");
        logger.debug("Static initalizer finished.");
    }

    /**
     * Standard constructor, super class provides basic functions as name, 
     * short description, usage and help message.
     * 
     */
    public Unload() {
        super("unload", "Unload configuration");
    }

    /**
     * Invoked from the central tool "MasterTool" to execute the load process.
     * 
     * @param args
     * @return
     */
    public boolean run(String[] args) {
        CommandLine line;
        try {
            line = new PosixParser().parse(OPTIONS, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            return false;
        }

        String[] packages = line.getArgs();
        if (packages.length == 0) {
            usage(OPTIONS, System.err);
            return false;
        }

        if (line.hasOption("config")) {
            // XXX: This just deletes everything.
            File conf = CCMResourceManager.getConfigDirectory();
            File[] files = conf.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    return !EXCLUDE.contains(file.getName());
                }
            });

            for (int i = 0; i < files.length; i++) {
                Files.delete(files[i]);
            }
        }

        return true;
    }

}
