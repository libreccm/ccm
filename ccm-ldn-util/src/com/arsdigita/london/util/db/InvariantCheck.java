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

package com.arsdigita.london.util.db;

import com.arsdigita.xml.XML;
import com.arsdigita.util.cmd.Program;
import com.arsdigita.london.util.Transaction;

import java.util.Iterator;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

public class InvariantCheck extends Program {

    private static final Logger s_log = Logger.getLogger(InvariantCheck.class);

    public InvariantCheck() {
        super("Invariant Check",
              "1.0.0",
              "");
    }
    
    protected void doRun(CommandLine cmdLine) {
        final String[] args = cmdLine.getArgs();
        
        Transaction txn = new Transaction() {
                public void doRun() {
                    InvariantHandler h = new InvariantHandler();
                    for (int i = 0 ; i < args.length ; i++) {
                        XML.parseResource(args[i], h);
                    }
                    
                    Iterator invariants = h.getInvariants();
                    while (invariants.hasNext()) {
                        Invariant inv = (Invariant)invariants.next();
                        if (s_log.isInfoEnabled()) {
                            s_log.info("Check: " + inv.getDescription());
                        }
                        try {
                            inv.check();
                        } catch (InvariantViolationException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            };
        txn.run();
    }

    public static void main(String[] args) {
        new InvariantCheck().run(args);
    }
}
