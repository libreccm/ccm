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
package com.arsdigita.london.util.cmd;

import com.arsdigita.london.util.Program;
import com.arsdigita.london.util.Transaction;
import com.arsdigita.persistence.CompoundFilter;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.domain.DomainObjectFactory;

import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentTypeLifecycleDefinition;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Date;

public class BulkPublish extends Program {

    private static final Logger s_log = Logger.getLogger(BulkPublish.class);
    private static final String SPACE = " ";

    public BulkPublish() {
        super("Bulk Publish",
              "1.0.0",
              "");

        Options options = getOptions();

        options.addOption(
                OptionBuilder.hasArgs().withLongOpt("types").withDescription(
                "Restrict publishing to items of the specified content types").
                create("t"));

        options.addOption(
                OptionBuilder.hasArg().withLongOpt("restrictToFolderId").
                withDescription(
                "Restrict publishing to items within the folder with the specified id").
                create("f"));

        options.addOption(
                OptionBuilder.hasArgs().withLongOpt("exceptionIds").
                withDescription("The ids of items that shouldn't be published").
                create("e"));


        options.addOption(
                OptionBuilder.hasArg(false).withLongOpt("ignore-errors").
                withDescription("Ignore any errors").create('i'));

    }

    /**
     * 
     * @param cmdLine
     */
    protected void doRun(CommandLine cmdLine) {
        final String[] types;
        final String[] exceptions;
        final int folderId;
        final boolean ignoreErrors = cmdLine.hasOption("i");

        if (cmdLine.hasOption("t")) {
            types = cmdLine.getOptionValues("t");

            System.out.println("Publishing items of types:");
            for (int i = 0; i < types.length; i++) {
                System.out.println(types[i]);
            }
        } else {
            types = null;
            System.out.println("Publishing all items");
        }

        if (cmdLine.hasOption("f")) {
            folderId = Integer.parseInt(cmdLine.getOptionValue("f"));
            Folder folder = new Folder(new OID(Folder.BASE_DATA_OBJECT_TYPE,
                                               folderId));
            System.out.println("Publishing items in folder: " + folder.
                    getDisplayName());
        } else {
            folderId = -1;
        }

        if (cmdLine.hasOption("e")) {
            exceptions = cmdLine.getOptionValues("e");
            System.out.print("Not publishing items with id: ");
            for (int i = 0; i < exceptions.length; i++) {
                System.out.print(exceptions[i] + SPACE);
            }
        } else {
            exceptions = null;
        }

        final List toPublish = new ArrayList();
        new Transaction() {

            public void doRun() {
                DataCollection items = SessionManager.getSession().retrieve(
                        ContentPage.BASE_DATA_OBJECT_TYPE);
                items.addNotEqualsFilter("type.id", null);
                items.addEqualsFilter("version", ContentItem.DRAFT);
                items.addOrder("title");

                FilterFactory filterFactory = items.getFilterFactory();

                if (folderId >= 0) {
                    Filter filter = filterFactory.simple(" ancestors like '%/"
                                                         + folderId + "/%'");
                    items.addFilter(filter);
                }

                if (null != exceptions) {
                    for (int n = 0; n < exceptions.length; n++) {
                        items.addFilter(filterFactory.notEquals("id",
                                                                exceptions[n]));
                    }
                }

                if (null != types) {

                    CompoundFilter or = filterFactory.or();

                    for (int i = 0; i < types.length; i++) {
                        or.addFilter(
                                filterFactory.equals("objectType", types[i]));
                    }

                    items.addFilter(or);
                }

                while (items.next()) {
                    toPublish.add(items.getDataObject().getOID());
                }
            }
        }.run();

        final Iterator items = toPublish.iterator();
        while (items.hasNext()) {
            final OID oid = (OID) items.next();
            Transaction txn = new Transaction() {

                public void doRun() {

                    ContentPage item = (ContentPage) DomainObjectFactory.
                            newInstance(oid);

                    if (s_log.isInfoEnabled()) {
                        s_log.info("Processing item " + oid + " "
                                   + item.getPath());
                    }

                    if (item.isLive()) {
                        s_log.info("Skipping because its already live");
                        return;
                    }

                    if (item.getContentSection() == null) {
                        s_log.warn(String.format(
                                "Content section of item "
                                + "'%s' (OID: %s') is null",
                                item.getPath(),
                                oid.toString()));
                    }

                    LifecycleDefinition def =
                                        ContentTypeLifecycleDefinition.
                            getLifecycleDefinition(
                            item.getContentSection(),
                            item.getContentType());

                    if (def == null) {
                        s_log.warn("Cannot publish item " + oid
                                   + " because it has no default lifecycle");
                        return;
                    }

                    ContentItem pending = item.publish(def, new Date());
                    pending.getLifecycle().start();
                }
            };
            try {
                txn.run();
            } catch (Throwable ex) {
                s_log.error("Cannot publish " + oid, ex);
                if (!ignoreErrors) {
                    return;
                }
            }
        }
    }

    public static void main(String[] args) {
        new BulkPublish().run(args);
    }
}
