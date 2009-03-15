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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.london.importer.DomainObjectMapper;
import com.arsdigita.london.importer.ImportParser;
import com.arsdigita.london.importer.ParserDispatcher;
import com.arsdigita.london.util.Transaction;
import com.arsdigita.packaging.Program;
import com.arsdigita.util.UncheckedWrapperException;

/**
 *  Standalone command-line tool which invokes the importer.
 * It can be invoked by:
 * <pre>
 * ccm-run com.arsdigita.london.importer.cms.ItemImportTool \
 *   master-import.xml /dir/with/files/to/include /dir/containing/lobs
 * </pre>
 *
 *  @see com.arsdigita.london.importer
 */
public class ItemImportTool extends Program {

    private static final Logger s_log = Logger.getLogger(ItemImportTool.class);

    public ItemImportTool() {
        super("Item Import Tool",
              "1.0.0",
              "INDEX-FILE ITEM-DIR ASSET-DIR");
    }

    public ItemImportTool(boolean startup) {
        super("Item Import Tool",
              "1.0.0",
              "INDEX-FILE ITEM-DIR ASSET-DIR",
              startup);
    }

    protected void doRun(CommandLine cmdLine) {
        final String[] args = cmdLine.getArgs();
        if (args.length != 3) {
            help(System.err);
            System.exit(1);
        }

        final String masterFile = args[0];
        final File itemDir = new File(args[1]);
        final File assetDir = new File(args[2]);

        final DomainObjectMapper mapper = new DomainObjectMapper();

        final ContentSection section = (ContentSection)ContentSection
            .retrieveApplicationForPath("/content/");

        final List items = new ArrayList();

        Transaction session = new Transaction() {
                public void doRun() {
                    ParserDispatcher parser = new ParserDispatcher();
                    parser.addParser(new ImportParser(mapper));
                    parser.addParser(new FolderItemParser(section, items));
                    parser.addParser(new ItemParser(assetDir, mapper));
                    parser.execute(masterFile);
                }
            };
        session.run();

        Iterator lazyItems = items.iterator();
        while (lazyItems.hasNext()) {
            Object[] entry = (Object[])lazyItems.next();
            final Folder folder = (Folder)entry[0];
            final String file = (String)entry[1];

            Transaction itemTransaction = new Transaction() {
                    public void doRun() {
                        ItemParser itemParser =
                            new ItemParser(assetDir, mapper);

                        File itemFile = new File(itemDir, file);
                        
                        if (s_log.isInfoEnabled()) {
                            s_log.info("Loading " + file + 
                                       " into " + folder.getPath());
                        }

                        ParserDispatcher parser = new ParserDispatcher();
                        parser.addParser(itemParser);
                        try {
                            parser.execute(itemFile.getCanonicalPath());
                        } catch (IOException ex) {
                            throw new UncheckedWrapperException(
                                "cannot process file" + file, ex);
                        }

                        ContentItem item = (ContentItem)itemParser.getDomainObject();
                        // returning the null item indicates an item that
                        // has already been imported
                        if (s_log.isDebugEnabled()) {
                            s_log.debug("Item is " + item);
                        }
                        if (item != null) {
                            // We have to place this item into language bundle
                            ContentBundle bundle = new ContentBundle(item);
                            bundle.setParent(folder);
                            if (s_log.isDebugEnabled()) {
                                s_log.error("Set bundle " + bundle);
                            }
                        }
                    }
                };
            itemTransaction.run();
        }
    }

    public static final void main(String[] args) {
        new ItemImportTool().run(args);
    }

}
