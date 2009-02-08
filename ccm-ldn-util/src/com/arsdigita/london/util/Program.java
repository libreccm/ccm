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

package com.arsdigita.london.util;

// unused imports
// import com.arsdigita.runtime.Startup;

//import org.apache.commons.cli.Options;
// import org.apache.commons.cli.HelpFormatter;
// import org.apache.commons.cli.OptionBuilder;
// import org.apache.commons.cli.GnuParser;
// import org.apache.commons.cli.ParseException;
// import org.apache.commons.cli.CommandLine;

// import java.io.OutputStream;
// import java.io.PrintWriter;

/**
 * 
 * @deprecated - use {@link com.arsdigita.packaging.Program}
 * pboy (Jan 2009):
 * This replacement might be a bad idea. Program is used by a) the packaging
 * programs during installation, configuration and updates and b) by all
 * modules which provide a cli interface for bulk tasks (e.g. the import
 * program). The latter may not be considered to a typical packaging task.
 */
public abstract class Program extends com.arsdigita.packaging.Program {
    
    public Program(String name,
                   String version,
                   String usage) {
        super(name, version, usage, true);
    }

    public Program(String name,
                   String version,
                   String usage,
                   boolean startup) {
        super(name, version, usage, startup);
    }

}
