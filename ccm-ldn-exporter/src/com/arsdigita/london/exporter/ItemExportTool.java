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

package com.arsdigita.london.exporter;

import java.io.File;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.web.Application;
import com.arsdigita.web.Web;
import com.arsdigita.london.util.Program;
import com.arsdigita.london.util.Transaction;

import org.apache.commons.cli.CommandLine;


public class ItemExportTool extends Program {

    public ItemExportTool() {
        super("Item Export Tool",
              "1.0.0",
              "ITEM-DIR ASSET-DIR CONTENT-SECTION");
    }
    
    public void doRun(CommandLine cmdLine) {
        final String[] args = cmdLine.getArgs();
        if (args.length != 3) {
            help(System.err);
            System.exit(1);
        }

        File itemDir = new File(args[0]);
        if (!itemDir.exists()) {
            itemDir.mkdir();
        }
        File assetDir = new File(args[1]);
        if (!assetDir.exists()) {
            assetDir.mkdir();
        }

        final ContentExporter exporter = new ContentExporter(itemDir,
                                                             assetDir);
        
        Transaction txn = new Transaction() {
                public void doRun() {
                    ContentSection section = (ContentSection)Application
                        .retrieveApplicationForPath(args[2]);
                    
                    exporter.exportManifest(section, 
                                            ContentItem.DRAFT,
                                            Web.getConfig().getHost().toString());
                }
            };
        txn.run();

        exporter.exportItems();
    }

    public static void main(String[] args) {
        new ItemExportTool().run(args);
    }
}
