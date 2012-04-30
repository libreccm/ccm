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

package com.arsdigita.london.importer.cms;

import java.io.File;

import org.apache.commons.cli.CommandLine;

import com.arsdigita.london.importer.DomainObjectMapper;
import com.arsdigita.london.importer.ImportParser;
import com.arsdigita.london.importer.ParserDispatcher;
import com.arsdigita.london.util.Transaction;
import com.arsdigita.util.cmd.Program;


/**
 *  Standalone command-line tool which runs the importer over
 * data set containing CMS assets.
 * It can be invoked by:
 * <pre>
 * ccm-run com.arsdigita.london.importer.cms.AssetImportTool \
 *   master-asset-import.xml /dir/containing/lobs
 * </pre>
 *
 *  @see com.arsdigita.london.importer
 */
public class AssetImportTool extends Program {

    public AssetImportTool() {
        super("Asset Import Tool",
              "1.0.0",
              "INDEX-FILE ASSET-DIR");
    }

    protected void doRun(CommandLine cmdLine) {
        final String[] args = cmdLine.getArgs();
        if (args.length != 2) {
            help(System.err);
            System.exit(1);
        }
        final DomainObjectMapper mapper = new DomainObjectMapper();

        Transaction session = new Transaction() {
                public void doRun() {
                    ParserDispatcher parser = new ParserDispatcher();
                    parser.addParser(new ImportParser(mapper));
                    parser.addParser(new AssetParser(new File(args[1]), mapper));
                    parser.execute(args[0]);
                }
            };
        session.run();
    }

    public static final void main(String[] args) {
        new AssetImportTool().run(args);
    }

}
