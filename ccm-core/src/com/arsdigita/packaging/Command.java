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
import java.io.PrintWriter;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

/**
 * Helper class to provide basic functions all commands need to have.
 *
 * Provides name, short description, usage and help information.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 */
abstract class Command {

    private String m_name;
    private String m_summary;
    private boolean m_verbose;

    /**
     * Represetents basic components of a command (command line interface).
     *  
     * @param name  Name of the command to execute as string
     * @param summary Short Description of the command as string
     */
    protected Command(String name, String summary) {
        m_name = name;
        m_summary = summary;
    }

    public String getName() {
        return m_name;
    }

    public String getSummary() {
        return m_summary;
    }

    void setVerbose(boolean value) {
        m_verbose = value;
    }

    public boolean isVerbose() {
        return m_verbose;
    }

    public abstract boolean run(String[] args);

    void usage(Options options, PrintStream out) {
        usage(options, out, "ccm " + getName(), null);
    }

    void usage(Options options, PrintStream out, String args) {
        usage(options, out, "ccm " + getName(), args);
    }

    static void usage(Options options, PrintStream out, String command,String args) {
        String str;
        final String debugVar = 
              "To show debugging output set the CCM_TOOLS_DEBUG environmental variable";
        if (args == null) {
            str = command;
        } else {
            str = command + " " + args;
        }

        HelpFormatter fmt = new HelpFormatter();
        PrintWriter w = new PrintWriter(out);
        fmt.printHelp(w, 80, str, "\n"+debugVar+"\n\nOptions:", 
                      options, 2, 4, null, true);
        w.flush();
    }

    static Options getOptions() {
        Options options = new Options();
        options.addOption
            (OptionBuilder
             .hasArg(false)
             .withLongOpt("usage")
             .withDescription("Print this message")
             .create("usage"));
        options.addOption
            (OptionBuilder
             .hasArg(false)
             .withLongOpt("help")
             .withDescription("Print this message")
             .create("help"));
        return options;
    }

}
