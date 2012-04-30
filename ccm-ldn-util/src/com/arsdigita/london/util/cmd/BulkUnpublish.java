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

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.Folder;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.CompoundFilter;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.util.cmd.Program;
import com.arsdigita.london.util.Transaction;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class BulkUnpublish extends Program {

    private static final Logger s_log = Logger.getLogger(BulkUnpublish.class);
    protected int folderId;
    protected String[] types;
    protected boolean ignoreErrors;
    protected String language;

    public BulkUnpublish(String name, String version) {
        super(name, version, "");

        Options options = getOptions();

        options.addOption(
                OptionBuilder.hasArgs().withLongOpt("types").withDescription(
                "Restrict operation to items of the specified content types").
                create("t"));
        options.addOption(
                OptionBuilder.hasArg().withLongOpt("restrictToFolderId").
                withDescription(
                "Restrict operation to items within the folder with the specified id").
                create("f"));

        options.addOption(
                OptionBuilder.hasArg(false).withLongOpt("ignore-errors").
                withDescription("Ignore any errors").create('i'));

        options.addOption(
                OptionBuilder.hasArg().withLongOpt("language").withDescription(
                "Restrict publishing to items with the specified langauge").
                create("l"));
    }

    protected void doRun(CommandLine cmdLine) {
        this.ignoreErrors = cmdLine.hasOption("i");

        if (cmdLine.hasOption("t")) {
            this.types = cmdLine.getOptionValues("t");

            System.out.println("To unpublish live items of type:");
            for (int i = 0; i < this.types.length; i++) {
                System.out.println(this.types[i]);
            }
        } else {
            this.types = null;
            System.out.println("To unpublish without item type restriction");
        }
        if (cmdLine.hasOption("f")) {
            this.folderId = Integer.parseInt(cmdLine.getOptionValue("f"));
            Folder folder = new Folder(new OID(Folder.BASE_DATA_OBJECT_TYPE,
                                               this.folderId));
            System.out.println("To unpublish items in folder: " + folder.
                    getDisplayName());
        } else {
            System.out.println(
                    "To unpublish items without any folder restriction");
            this.folderId = -1;
        }

        if (cmdLine.hasOption("l")) {
            language = cmdLine.getOptionValue("l");
            System.out.printf("Publishing only items with language: %s\n",
                              language);
        } else {
            language = null;
        }

        final List toProcess = getListToProcess(
                ContentPage.BASE_DATA_OBJECT_TYPE);
        System.out.println("Processing " + toProcess.size() + " items.");
        unpublish(toProcess);
    }

    public static void main(String[] args) {
        new BulkUnpublish("Bulk Unpublish", "1.0.0").run(args);
    }

    protected List getListToProcess(final String baseObjectType) {
        final List toProcess = new ArrayList();
        final int folderId = this.folderId;
        final String[] types = this.types;

        new Transaction() {

            public void doRun() {
                DataCollection items = SessionManager.getSession().retrieve(
                        baseObjectType);
                if (!baseObjectType.equals(Folder.BASE_DATA_OBJECT_TYPE)) {
                    items.addNotEqualsFilter("type.id", null);
                }
                //items.addOrder("title");

                FilterFactory filterFactory = items.getFilterFactory();

                if (folderId >= 0) { //TODO could add logic to fetch master version if required.
                    Filter filter = filterFactory.simple(" ancestors like '%/"
                                                         + folderId + "/%'");
                    items.addFilter(filter);
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
                    ContentItem page = (ContentItem) DomainObjectFactory.
                            newInstance(items.getDataObject());
                    toProcess.add(page.getDraftVersion().getOID());
                }
            }
        }.run();

        return toProcess;
    }

    protected void unpublish(List toProcess) {
        final Iterator items = toProcess.iterator();
        while (items.hasNext()) {
            final OID oid = (OID) items.next();
            unpublish(oid);
        }
    }

    protected void unpublish(final OID oid) {
        final boolean ignoreErrors = this.ignoreErrors;

        Transaction txn = new Transaction() {

            public void doRun() {
                ContentPage item = (ContentPage) DomainObjectFactory.newInstance(
                        oid);
                ContentBundle bundle = item.getContentBundle();
                Collection<String> langs = bundle.getLanguages();
                for (String lang : langs) {
                    if ((language == null)
                        || language.isEmpty()
                        || lang.equals(language)) {
                        ContentItem toUnPublish = bundle.getInstance(lang);
                        System.out.println("Unpublishing item " + oid + " " + toUnPublish.
                                getPath());
                        item.setLive(null);
                    }
                }
            }
        };
        try {
            txn.run();
        } catch (Throwable ex) {
            s_log.error("Cannot unpublish " + oid, ex);
            if (!ignoreErrors) {
                return;
            }
        }
    }
}
