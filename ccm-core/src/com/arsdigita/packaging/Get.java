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
import com.arsdigita.util.JavaPropertyWriter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ParameterWriter;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;

/**
 * Get
 * 
 * Implements the command line interface to list one or more values
 * from a CCM configuration database (registry)
 * 
 * Called by ccm get command
 * 
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/
class Get extends Command {

    public static final Logger logger = Logger.getLogger(Get.class);
    public final static String versionId =
                               "$Id: Get.java 1324 2006-09-21 22:13:16Z apevec $"
                               + " by $Author: apevec $, "
                               + "$DateTime: 2004/08/16 18:10:38 $";
    private static final Options OPTIONS = getOptions();

    static {
        logger.debug("Static initalizer starting...");
        OptionGroup group = new OptionGroup();
        group.addOption(OptionBuilder.hasArg(false).withLongOpt("all").
                withDescription("Lists all configuration parameters").create(
                "all"));
        group.addOption(OptionBuilder.hasArg().withArgName("PARAMETER").
                withLongOpt("value").withDescription(
                "Prints a scalar value without the key").create("value"));
        OPTIONS.addOptionGroup(group);
        logger.debug("Static initalizer finished.");
    }

    public Get() {
        super("get", "Print one or more values from a CCM "
                     + "configuration database");
    }

    public boolean run(String[] args) {
        CommandLine line;
        try {
            line = new PosixParser().parse(OPTIONS, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            return false;
        }

        if (args.length == 0) {
            usage(OPTIONS, System.err, "[PARAMETERS]");
            return false;
        }

        if (line.hasOption("usage") || line.hasOption("help")) {
            usage(OPTIONS, System.out, "[PARAMETERS]");
            return true;
        }

        ConfigRegistry reg = new ConfigRegistry();
        Config config = new Config(reg);
        config.load(System.err);

        String[] names;
        if (line.hasOption("value")) {
            names = new String[]{line.getOptionValue("value")};
            if (line.getArgs().length > 0) {
                System.err.println("--value option does not allow parameters");
                return false;
            }
        } else {
            names = line.getArgs();
        }

        List parameters;

        if (line.hasOption("all")) {
            if (names.length > 0) {
                System.err.println("--all option does not allow parameters");
                return false;
            }
            parameters = config.getParameters();
        } else {
            parameters = new ArrayList();
            boolean err = false;
            for (int i = 0; i < names.length; i++) {
                String name = names[i];
                Parameter param = config.getParameter(name);
                if (param == null) {
                    System.err.println("no such parameter: " + name);
                    err = true;
                } else {
                    parameters.add(param);
                }
            }
            if (err) {
                return false;
            }
        }

        for (Iterator it = parameters.iterator(); it.hasNext();) {
            Parameter param = (Parameter) it.next();
            Object value = config.get(param);
            Properties props = new Properties();
            ParameterWriter writer = new JavaPropertyWriter(props);
            param.write(writer, value);
            if (line.hasOption("value")) {
                if (props.size() > 1) {
                    System.err.println("not a scalar: " + param.getName());
                    return false;
                }
                System.out.println(props.values().iterator().next());
            } else {
                try {
                    write(props, System.out);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                    return false;
                }
            }
        }

        return true;
    }

    private void write(Properties properties, PrintStream out)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        properties.store(baos, null);
        BufferedReader reader =
                       new BufferedReader(new StringReader(baos.toString()));
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                return;
            }
            if (line.trim().startsWith("#")) {
                continue;
            }
            out.println(line);
        }
    }
}
