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

package com.arsdigita.london.terms;

import org.apache.commons.cli.CommandLine;

import com.arsdigita.london.util.Transaction;
import com.arsdigita.util.cmd.Program;
import com.arsdigita.web.Application;


public class SetRoot extends Program {
    
    public SetRoot() {
        super("Terms Set Category Root",
              "1.0.0",
              "DOMAIN-KEY APPLICATION-PATH [ROOT-CONTEXT]");
    }
    
    protected void doRun(CommandLine cmdLine) {
        final String[] args = cmdLine.getArgs();
        if (args.length != 2 &&
            args.length != 3) {
            help(System.err);
            System.exit(1);
        }

        Transaction txn = new Transaction() {
            protected void doRun() {
                String key = args[0];
                String path = args[1];
                String context = args.length == 3 ? args[2] : null;
                
                Application app = Application.retrieveApplicationForPath(path);
                Domain domain = Domain.retrieve(key);
                domain.setAsRootForObject(app,
                                          context);
                domain.save();
            }
        };
        txn.run();
    }
    
    public static void main(String[] args) {
        new SetRoot().run(args);
    }
}
