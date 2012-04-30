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

import com.arsdigita.london.terms.importer.Parser;
import com.arsdigita.london.util.Transaction;
import com.arsdigita.util.cmd.Program;

public class Importer extends Program {
    
    public Importer() {
        super("Term Importer",
              "1.0.0",
              "CONTROLLED-LIST");
    }
    
    protected void doRun(CommandLine cmdLine) {
        final String[] args = cmdLine.getArgs();
        if (args.length < 1) {
            help(System.err);
            System.exit(1);
        }

        Transaction txn = new Transaction() {
            protected void doRun() {
                Parser parser = new Parser();
                for (int i = 0 ; i < args.length ; i++) {
                    parser.parse(args[i]);
                }
            }
        };
        txn.run();
    }
    
    public static void main(String[] args) {
        new Importer().run(args);
    }
}
