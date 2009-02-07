/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
import com.arsdigita.util.parameter.Parameter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * Clear
 *
 * Implements the command line interface to clear one or more values
 * in a CCM configuration database (registry)

 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Clear.java 736 2005-09-01 10:46:05Z sskracic $
 */
class Clear extends Command {
    public final static String versionId =
        "$Id: Clear.java 736 2005-09-01 10:46:05Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Options s_options = getOptions();

    public Clear() {
        super("clear",
              "Clear one or more values in a CCM configuration database");
    }

    public boolean run(final String[] args) {
        final CommandLine line;

        try {
            line = new PosixParser().parse(s_options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            return false;
        }

        if (args.length == 0) {
            usage(s_options, System.err, "PARAMETERS");
            return false;
        }

        if (line.hasOption("usage") || line.hasOption("help")) {
            usage(s_options, System.out, "PARAMETERS");
            return true;
        }

        final ConfigRegistry reg = new ConfigRegistry();
        final Config config = new Config(reg);
        config.load(System.err);

        final String[] keys = line.getArgs();
        final List errors = new ArrayList();
        final List params = new ArrayList();

        for (int i = 0; i < keys.length; i++) {
            final Parameter param = config.getParameter(keys[i]);

            if (param == null) {
                errors.add("no such parameter: " + keys[i]);
            } else {
                params.add(param);
            }
        }

        if (errors.isEmpty()) {
            final Iterator iter = params.iterator();

            while (iter.hasNext()) {
                config.set((Parameter) iter.next(), null);
            }

            if (config.validate(System.err)) {
                try {
                    config.save();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                    return false;
                }

                return true;
            } else {
                return false;
            }
        } else {
            final Iterator iter = errors.iterator();

            while (iter.hasNext()) {
                System.out.println((String) iter.next());
            }

            return false;
        }
    }
}
