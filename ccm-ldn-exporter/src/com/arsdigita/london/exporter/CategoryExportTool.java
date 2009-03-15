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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;

import com.arsdigita.categorization.Category;
import com.arsdigita.london.util.Transaction;
import com.arsdigita.packaging.Program;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Application;

public class CategoryExportTool extends Program {

    boolean exportItems = false;
    
    public CategoryExportTool() {
        super("Category Export Tool",
              "1.0.1",
              "[app URL] [context] [export dir] [key] [url] [title] [version]");
        getOptions().addOption
        (OptionBuilder
         .hasArg(false)
         .withLongOpt("items")
         .withDescription("Export items/terms mappings")
         .create('i'));
    }

    public void doRun(CommandLine cmdLine) {
        if (cmdLine.hasOption('i')) {
            exportItems = true;
        }
        String[] args = cmdLine.getArgs();
        if (args.length != 7) {
            for (int i = 0 ; i < args.length ; i++) {
                System.out.print("arg"+i+"='" + args[i]+"' ");
            }
            help(System.err);
            System.exit(1);
        }

        final File catDir = new File(args[2]);
        if (!catDir.exists() && !catDir.mkdir()) {
            System.err.println("Could not mkdir " + catDir);
            return;
        }

        final String app = args[0];
        final String context = args[1];
        final String key = args[3];
        final String url = args[4];
        final String title = args[5];
        final String version = args[6];
        
        Transaction txn = new Transaction() {
                public void doRun() {
                    Application appl = Application.retrieveApplicationForPath(app);
                    Category root;
                    if ("DEFAULT".equals(context)) {
                        root = Category.getRootForObject(appl, null);
                    } else {
                        root = Category.getRootForObject(appl, context);
                    }
                    
                    CategoryExporter catExporter = new CategoryExporter(catDir);
                    try {
                        catExporter.export(root, 
                                           key,
                                           new URI(url),
                                           title,
                                           null,
                                           version,
                                           new Date(),
                                           exportItems);
                    } catch (URISyntaxException ex) {
                        throw new UncheckedWrapperException("Cannot parse url " + url, ex);
                    }
                }
            };
        txn.run();
    }

    public static void main(String[] args) {
        new CategoryExportTool().run(args);
    }
}
