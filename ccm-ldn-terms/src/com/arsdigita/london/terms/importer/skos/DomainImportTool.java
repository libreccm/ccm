/*
 * Copyright (C) 2009 Permeance Technologies Pty Ltd. All Rights Reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package com.arsdigita.london.terms.importer.skos;

import java.sql.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.london.util.Transaction;
import com.arsdigita.packaging.Program;
import com.arsdigita.util.WrappedError;

/**
 * A tool for importing domains from an SKOS file.
 * 
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
public class DomainImportTool extends Program {
    public DomainImportTool() {
        super("Domain importer", "1.0.0", "skos-filename.rdf key title description version YYYY-MM-DD");
    }

    protected void doRun(CommandLine cmdLine) {
        final String[] args = cmdLine.getArgs();
        if (args.length != 6) {
            help(System.err);
            System.exit(1);
        }

        s_log.info("Importing domain from SKOS file " + args[0]);

        final String key = args[1];
        final String title = args[2];
        final String description = args[3];
        final String version = args[4];
        final Date released = Date.valueOf(args[5]);

        Transaction txn = new Transaction() {
            protected void doRun() {
                DomainParser parser = new DomainParser(key, title, description, version, released);
                parser.parse(args[0]);
            }
        };
        try {
            txn.run();
        } catch (RuntimeException e) {
            s_log.error("RDF importer failed unexpectedly", e);
            throw e;
        } catch (WrappedError e) {
            s_log.error("RDF importer failed unexpectedly", e);
            throw e;
        }
    }

    public static void main(final String[] args) {
        new KernelExcursion() {
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                new DomainImportTool().run(args);
            }
        }.run();
    }

    private static final Logger s_log = Logger.getLogger(DomainImportTool.class);
}
