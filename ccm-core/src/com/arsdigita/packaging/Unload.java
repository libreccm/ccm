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

/**
 * Unload
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/

class Unload extends Command {

    public final static String versionId = 
            "$Id: Unload.java 736 2005-09-01 10:46:05Z sskracic $" +
            " by $Author: sskracic $, " +
            "$DateTime: 2004/08/16 18:10:38 $";

    private static final Options OPTIONS = new Options();

    static {
        OPTIONS.addOption
            (OptionBuilder
             .hasArg(false)
             .withLongOpt("config")
             .withDescription("Unload configuration")
             .create());
    }

    private static final Set EXCLUDE = new HashSet();

    static {
        EXCLUDE.add("resin.conf");
        EXCLUDE.add("resin.pid");
        EXCLUDE.add("server.xml");
    }

    public Unload() {
        super("unload", "Unload configuration");
    }

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
