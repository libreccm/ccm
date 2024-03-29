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

import com.arsdigita.runtime.ConfigRegistry;
import com.arsdigita.util.JavaPropertyReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;

/**
 * Set
 *
 * Implements the command line interface to modify one or more values
 * in a CCM configuration database (registry)
 * 
 * Called by ccm set command
 * 
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/08/16 $
 * @version $Id: Set.java 736 2005-09-01 10:46:05Z sskracic $
 */
class Set extends Command {

    private final static Logger logger = Logger.getLogger(Set.class);

    private static final Options OPTIONS = getOptions();

    static {
        logger.debug("Static initalizer starting...");
        OPTIONS.addOption
            (OptionBuilder
             .hasArg(false)
             .withLongOpt("interactive")
             .withDescription("Interactively edit configuration values")
             .create());
        logger.debug("Static initalizer finished.");
    }

    /**
     * Constructor
     */
    public Set() {
        super("set", "Set one or more values in a CCM configuration database");
    }

    /**
     * Run script.
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

        if (args.length == 0) {
            usage(OPTIONS, System.err, "[KEY=VALUE...]");
            return false;
        }

        if (line.hasOption("usage") || line.hasOption("help")) {
            usage(OPTIONS, System.out, "[KEY=VALUE...]");
            return true;
        }

        ConfigRegistry reg = new ConfigRegistry();
        Config config = new Config(reg);
        config.load(System.err);
        
        Properties props;
        try {
            props = Load.argsToProperties(line.getArgs());
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }

        boolean valid = true;
        for (Iterator it = props.keySet().iterator(); it.hasNext(); ) {
            String param = (String) it.next();
            if (config.getParameter(param) == null) {
                System.out.println("no such parameter: " + param);
                valid = false;
            }
        }

        if (!valid) { return false; }

        if (!config.load(new JavaPropertyReader(props), System.err)) {
            return false;
        }

        if (line.hasOption("interactive")) {
            ParameterEditor editor = new ParameterEditor
                (config, System.in, System.out);
            if (!editor.edit()) {
                return true;
            }
        } else if (!config.validate(System.err)) {
            return false;
        }

        try {
            config.save();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }

        return true;
    }

}
