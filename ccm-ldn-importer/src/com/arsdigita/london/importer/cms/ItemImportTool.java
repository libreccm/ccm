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
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.london.importer.DomainObjectMapper;
import com.arsdigita.london.importer.ImportParser;
import com.arsdigita.london.importer.ParserDispatcher;
import com.arsdigita.london.util.Transaction;
import com.arsdigita.util.cmd.Program;
import com.arsdigita.util.UncheckedWrapperException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  Standalone command-line tool which invokes the importer.
 * It can be invoked by:
 * </p>
 * <pre>
 * ccm-run com.arsdigita.london.importer.cms.ItemImportTool \
 *   master-import.xml /dir/with/files/to/include /dir/containing/lobs content-section-name
 * </pre>
 * <p>
 * <strong>Attention:</strong> The importer was modified to support import of
 * items with more than one language version. The modifications for this are:
 * </p>
 * <ul>
 * <li>A {@link Map} of the already created {@link ContentBundle}s is kept.</li>
 * <li>To identify language versions, the imported content items must have a
 * name of the following pattern: <code>name-language</code>, for example
 * <code>about-de</code> and <code>about-en</code>.</li>
 * <li>When the item is added, the importer now checks if there is
 * already a content bundle for the item. If not a new one is created, if there
 * is one, the item is added as an instance.</li>
 * <li>For items with only one language version the importer should work as
 * before.</li>
 * </ul>
 *
 *  @see com.arsdigita.london.importer
 */
public class ItemImportTool extends Program {

    private static final Logger s_log = Logger.getLogger(ItemImportTool.class);

    public ItemImportTool() {
        super("Item Import Tool",
              "1.0.0",
              "INDEX-FILE ITEM-DIR ASSET-DIR CONTENT-SECTION");
    }

    public ItemImportTool(boolean startup) {
        super("Item Import Tool",
              "1.0.0",
              "INDEX-FILE ITEM-DIR ASSET-DIR CONTENT-SECTION",
              startup);
    }

    protected void doRun(CommandLine cmdLine) {
        try {
            final String[] args = cmdLine.getArgs();
            if (args.length != 4) {
                help(System.err);
                System.exit(1);
            }

            final String masterFile = args[0];
            final File itemDir = new File(args[1]);
            final File assetDir = new File(args[2]);
            final ContentSection section = getContentSection(args[3]);

            final DomainObjectMapper mapper = new DomainObjectMapper();

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
            /*
             * Extension to support import of items in more than one language
             * Jens Pelzetter 2010-10-21
             */
            final Map<String, ContentBundle> bundles;
            bundles = new HashMap<String, ContentBundle>();
            /*
             * Extension end
             */
            while (lazyItems.hasNext()) {
                Object[] entry = (Object[]) lazyItems.next();
                final Folder folder = (Folder) entry[0];
                final String file = (String) entry[1];

                Transaction itemTransaction = new Transaction() {

                    public void doRun() {
                        ItemParser itemParser =
                                   new ItemParser(assetDir, mapper);

                        File itemFile = new File(itemDir, file);

                        if (s_log.isInfoEnabled()) {
                            s_log.info("Loading " + file + " into "
                                       + folder.getPath());
                        }

                        ParserDispatcher parser = new ParserDispatcher();
                        parser.addParser(itemParser);
                        try {
                            parser.execute(itemFile.getCanonicalPath());
                        } catch (IOException ex) {
                            throw new UncheckedWrapperException(
                                    "cannot process file" + file, ex);
                        }

                        ContentItem item =
                                    (ContentItem) itemParser.getDomainObject();
                        if (item == null) {
                            s_log.warn("item is null. Igoring...");
                            return;
                        }
                        s_log.debug(String.format("Got item from ItemParser:"));
                        s_log.debug(String.format("OID  : %s", item.getOID()));
                        s_log.debug(String.format("Name : %s", item.getName()));
                        s_log.debug(String.format("Item file name : %s", itemFile.
                                getName()));
                        s_log.debug(
                                String.format("Title: %s", item.get("title")));
                        if (item instanceof ContentPage) {
                            s_log.debug("Item is a content page...");
                        }
                        /*
                         * Multi lang extension begin
                         */
                        String itemName = itemFile.getName().substring(
                                0, itemFile.getName().length() - 4);
                        String bundleName;
                        s_log.debug(String.format("Using item name '%s'...",
                                                  item.getName()));
                        if (itemName.lastIndexOf('-') == -1) {
                            s_log.debug(
                                    "No '-' in name, using name as bundle name");
                            bundleName = itemName;
                        } else {
                            s_log.debug(
                                    "Found a '-' in the, name, using part before '-' as bundle name.");
                            if (itemName.substring((itemName.lastIndexOf('-')
                                                    + 1)).
                                    equals("de")
                                || itemName.substring((itemName.lastIndexOf('-')
                                                       + 1)).
                                    equals("en")) {
                                bundleName = itemName.substring(0, itemName.
                                        lastIndexOf('-'));
                                s_log.debug(String.format(
                                        "Created bundle name: '%s'", bundleName));
                            } else {
                                s_log.debug(
                                        "Part behind the last '-' is not 'de' or 'en', using item name as bundle name");
                                bundleName = itemName;
                            }
                        }
                        /*
                         * Multi lang extension end
                         */
                        // returning the null item indicates an item that
                        // has already been imported
                        if (s_log.isDebugEnabled()) {
                            s_log.debug("Item is " + item);
                        }
                        if (item != null) {
                            // We have to place this item into language bundle
                            /*
                             * Multi lang extension start
                             */
                            ContentBundle bundle;
                            s_log.debug(String.format("Bundle name: %s",
                                                      bundleName));
                            if (itemName.lastIndexOf('-') == -1) {
                                s_log.debug("Item is not localized...");
                                bundle = new ContentBundle(item);
                                bundle.setParent(folder);
                                bundle.setName(bundleName);
                                bundles.put(bundleName, bundle);
                            } else {
                                s_log.debug("Item is localized...");
                                bundle = bundles.get(bundleName);
                                if (bundle == null) {
                                    s_log.debug(
                                            "No content bundle found for item, creating new.");
                                    bundle = new ContentBundle(item);
                                    bundle.setParent(folder);
                                    bundle.setName(bundleName);
                                    bundles.put(bundleName, bundle);
                                } else {
                                    s_log.debug(
                                            "Found content bundle for item, adding item as instance.");
                                    bundle.addInstance(item);
                                }
                            }
                            /*
                             * Extension end
                             */
                            System.out.println("Set bundle " + bundle);
                            if (s_log.isDebugEnabled()) {
                                s_log.debug("Set bundle " + bundle);
                            }
                        }
                    }
                };
                itemTransaction.run();
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    public static final void main(String[] args) {
        try {
            new ItemImportTool().run(args);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get the content section for the entered path, adding "/" prefix and suffix if necessary.
     * 
     * @param rawPath the raw path of the content section, e.g. "content".
     * 
     * @return the content section
     */
    private ContentSection getContentSection(String rawPath) {
        final StringBuilder path = new StringBuilder();
        if (!rawPath.startsWith("/")) {
            path.append("/");
        }
        path.append(rawPath);
        if (!rawPath.endsWith("/")) {
            path.append("/");
        }

        final ContentSection section = (ContentSection) ContentSection.
                retrieveApplicationForPath(path.toString());

        if (section == null) {
            throw new DataObjectNotFoundException("Content section not found with path "
                                                  + path);
        }
        return section;
    }
}
