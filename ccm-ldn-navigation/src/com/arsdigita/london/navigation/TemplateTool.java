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

package com.arsdigita.london.navigation;

import com.arsdigita.london.util.Program;
import com.arsdigita.london.util.Transaction;
import com.arsdigita.domain.DataObjectNotFoundException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;

import org.apache.log4j.Logger;

public class TemplateTool extends Program {
    
    private static final Logger s_log = Logger.getLogger(TemplateTool.class);

    TemplateTool() {
        super("Template Tool",
              "1.0.0",
              "TEMPLATE-PATH [TITLE]");
        
        getOptions().addOption(
            OptionBuilder
            .withLongOpt("delete")
            .withDescription("Delete the specified template")
            .create("d"));
    }
    
    protected void doRun(CommandLine cmdLine) {
        final String[] args = cmdLine.getArgs();
        final boolean delete = cmdLine.hasOption("d");

        if( args.length == 0 || args.length > 2 ) {
            help(System.err);
            System.exit(1);
        }

        final String path = args[0];
        
        new Transaction() {
            public void doRun() {
                Template temp = null;
                try {
                    temp = Template.retrieveByURL(path);
                } catch (DataObjectNotFoundException ex) {
                    if (!delete) {
                        if (s_log.isDebugEnabled()) {
                            s_log.debug("Creating template " + 
                                        path + " " + args[1]);
                        }
                        temp = Template.create(args[1],
                                               null,
                                               path);
                        temp.save();
                    } else {
                        s_log.warn("Cannot find template to be deleted " + path);
                    }
                }
                
                if (delete && temp != null) {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Deleting template " + path);
                    }
                    temp.delete();
                }
            }
        }.run();
    }

    public static final void main(String[] args) {
        new TemplateTool().run(args);
    }
}
