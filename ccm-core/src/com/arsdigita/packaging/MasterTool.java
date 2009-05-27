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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Main entry point: Implements the ccm command line tool.
 * 
 * Analyses the parameters, esp. the command part (1. parameter), and delegates 
 * the actual work to specializied classes for each command.
 * 
 * The main purpose is to be called from an operation system specific script
 * (or program) which is responsible for delivering the required parameters
 * and additional information (e.g. the location where to install the
 * application files via environment variable CCM_HOME). The currently
 * provided script implementation is ccm, a shell script (sh and bat) backed
 * by some PERL scripts, located in the tools directory of CCM trunk.
 * 
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: MasterTool.java 1169 2006-06-14 13:08:25Z fabrice $
 */
public class MasterTool {
    public static final String versionId =
        "$Id: MasterTool.java 1169 2006-06-14 13:08:25Z fabrice $" +
        "$Author: fabrice $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger(MasterTool.class);

    private static void usage(Commands cmds, PrintStream out) {
        out.println("usage: ccm [OPTIONS | COMMAND]");
        out.println();
        out.println("Options:");
        out.println("  --help     Display help");
        out.println("  --usage    Print this message");
		out.println();
		out.println("To show debugging output set the CCM_TOOLS_DEBUG environmental variable");
        out.println();
        out.println("Commands:");
        out.print(cmds.getCommands());
    }

    /**
     * Entry point for the the ccm command line tool.
     * 
     * Available commands:
     * - load:    loads the database schema and initial content
     * - hostinit:populates the applications directors (jsp, classes, etc)
     * - get:     retrieves a configuration parameter
     * - set:     stores a configuration parameeter
     * - upgrade: upgrades database (schema & content) and/or application files
     * - status:  execution status of the application
     * - which:   searches for a resource or class
     * - unload:
     *
     * @param args the command line arguments
     */

    public static final void main(final String[] args) {
        final PrintStream out = System.out;
        final PrintStream err = System.err;

        // nolonger needed
        // com.arsdigita.runtime.Startup.startup();

        Commands cmds = new Commands();
        Command help = new Help();
        Command usage = new Usage();
        cmds.add(help, true);
        cmds.add(usage, true);
        cmds.add(new Load());
        cmds.add(new Unload(), true);
        cmds.add(new Upgrade());
        cmds.add(new Get());
        cmds.add(new Set());
        cmds.add(new Clear());
        cmds.add(new Status());
        cmds.add(new Which());

        if (args.length == 0) {
            usage(cmds, err);
            System.exit(1);
        }

        String name = args[0];
        Command cmd = cmds.get(name);

        if (cmd == null) {
            err.println("no such command: " + name);
            System.exit(1);
        }

        String[] command = new String[args.length - 1];
        System.arraycopy(args, 1, command, 0, command.length);

        boolean result = cmd.run(command);
        if (cmd == help || cmd == usage) {
            usage(cmds, out);
        }
        if (result) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }

    private static final class Commands {

        private List m_commands = new ArrayList();
        private Map m_map = new HashMap();
        private int m_maxName = 0;
        private HashSet m_hidden = new HashSet();

        public Commands() {}

        public void add(Command command, boolean hidden) {
            m_commands.add(command);
            String name = command.getName();
            m_map.put(name, command);
            int length = name.length();
            if (length > m_maxName) {
                m_maxName = length;
            }
            if (hidden) {
                m_hidden.add(command);
            }
        }

        public void add(Command command) {
            add(command, false);
        }

        public Command get(String name) {
            return (Command) m_map.get(name);
        }

        public String getCommands() {
            StringBuffer result = new StringBuffer();
            for (Iterator it = m_commands.iterator(); it.hasNext(); ) {
                Command cmd = (Command) it.next();
                if (m_hidden.contains(cmd)) { continue; }
                String line = "  " + cmd.getName();
                result.append(line);
                for (int i = 0; i < m_maxName + 6 - line.length(); i++) {
                    result.append(" ");
                }
                result.append(cmd.getSummary() + "\n");
            }

            return result.toString();
        }

    }

}
