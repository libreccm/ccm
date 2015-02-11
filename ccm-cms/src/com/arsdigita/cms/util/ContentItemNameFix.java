/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
 *
 */
package com.arsdigita.cms.util;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.Folder;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.util.cmd.Program;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;

/**
 *
 * @author Jens Pelzetter
 */
@SuppressWarnings("PMD.SystemPrintln")
public class ContentItemNameFix extends Program {

    private boolean pretend = false;

    public ContentItemNameFix() {
        super("ContentItemNameFix", "1.0.0", "");

        getOptions().addOption(
            OptionBuilder
            .hasArg(false)
            .withLongOpt("pretend")
            .withDescription("Only show what would be done")
            .create("p"));
    }

    public static final void main(final String[] args) {
        new ContentItemNameFix().run(args);
    }

    @Override
    protected void doRun(final CommandLine cmdLine) {

        System.out.printf("Running ContentItemNameFix...\n");

        pretend = cmdLine.hasOption("p");

        if (pretend) {
            System.out.printf("Pretend option is on, only showing what would be done...\n\n");
        } else {
            System.out.print("\n");
        }

        new KernelExcursion() {

            @Override
            protected void excurse() {
                setEffectiveParty(Kernel.getSystemParty());

                final Session session = SessionManager.getSession();
                final TransactionContext transactionContext = session.getTransactionContext();

                transactionContext.beginTxn();

                final DataCollection draftFolders = session.retrieve(Folder.BASE_DATA_OBJECT_TYPE);
                draftFolders.addEqualsFilter(ContentItem.VERSION, "draft");
                
                while (draftFolders.next()) {
                    checkFolder(draftFolders.getDataObject());
                }
                
                final DataCollection draftBundles = session.retrieve(
                    ContentBundle.BASE_DATA_OBJECT_TYPE);
                draftBundles.addEqualsFilter(ContentItem.VERSION, "draft");

                while (draftBundles.next()) {
                    checkBundle(draftBundles.getDataObject());

                }

                transactionContext.commitTxn();

            }

        }.run();

    }

    private void checkFolder(final DataObject folderObj) {
        
        final Folder draftFolder = new Folder(folderObj);
        final Folder liveFolder = (Folder) draftFolder.getLiveVersion();
        
        if (liveFolder != null && !draftFolder.getName().equals(liveFolder.getName())) {
            System.out.printf("Problems with folder %s:/%s (id: %s):\n", 
                              draftFolder.getContentSection().getName(), 
                              draftFolder.getPath(), 
                              draftFolder.getID().toString());
            System.out.printf("\t Live Folder has wrong name: Is '%s' but should be '%s'.", 
                              liveFolder.getName(),
                              draftFolder.getName());
            
            if (pretend) {
                System.out.print("\n\n");
            } else {
                liveFolder.setName(draftFolder.getName());
                System.out.print(" Corrected.\n\n");
            }
            
        }
    }
    
    private void checkBundle(final DataObject bundleObj) {

        final ContentBundle draftBundle = new ContentBundle(bundleObj);
        final ContentItem primaryDraftItem = draftBundle.getPrimaryInstance();

        final String itemId = primaryDraftItem.getID().toString();
        final String itemPath = String.format("%s:/%s",
                                              primaryDraftItem.getContentSection().getName(),
                                              primaryDraftItem.getPath());

        final HeaderStatus headerStatus = new HeaderStatus();

        //This is our reference, all bundles, instances etc belonging to the item sould have this 
        //name
        final String itemName = primaryDraftItem.getName();

        if (!draftBundle.getName().equals(itemName)) {
            printItemHeaderLine(itemId, itemPath, headerStatus);

            System.out.printf(
                "\t Draft ContentBundle has wrong name: Is '%s' but should be '%s'.",
                itemName,
                draftBundle.getName());
            if (pretend) {
                System.out.print("\n");
            } else {
                draftBundle.setName(itemName);
                System.out.printf(" Corrected.\n");
            }
        }

        checkInstances(draftBundle, itemName, itemId, itemPath, headerStatus);

        final ContentBundle liveBundle = (ContentBundle) draftBundle.getLiveVersion();
        if (liveBundle != null) {
            if (!liveBundle.getName().equals(itemName)) {
                printItemHeaderLine(itemId, itemPath, headerStatus);

                System.out.printf(
                    "\tLive ContentBundle has wrong name. Should be '%s' but is '%s'",
                    itemName,
                    liveBundle.getName());

                if (pretend) {
                    System.out.print("\n");
                } else {
                    liveBundle.setName(itemName);
                    System.out.printf(" Corrected.\n");
                }
            }

            checkInstances(liveBundle, itemName, itemId, itemPath, headerStatus);
        }
        
        if (headerStatus.isHeaderPrinted()) {
            System.out.print("\n");
        }

    }

    private void checkInstances(final ContentBundle draftBundle,
                                final String itemName,
                                final String itemId,
                                final String itemPath,
                                final HeaderStatus headerStatus) {
        final ItemCollection instances = draftBundle.getInstances();
        ContentItem current;
        while (instances.next()) {
            current = instances.getContentItem();

            if (!itemName.equals(current.getName())) {
                printItemHeaderLine(itemId, itemPath, headerStatus);
                System.out.printf(
                    "\t%s instance %s (language: %s has wrong name. Should be '%s', but is '%s'.",
                    current.getVersion(),
                    current.getID().toString(),
                    current.getLanguage(),
                    itemName,
                    current.getName());
                if (pretend) {
                    System.out.print("\n");
                } else {
                    current.setName(itemName);
                    System.out.printf(" Corrected.\n");
                }
            }
        }

    }

    private class HeaderStatus {

        private boolean headerPrinted = false;

        public HeaderStatus() {
            //Nothing
        }

        public boolean isHeaderPrinted() {
            return headerPrinted;
        }

        public void setHeaderPrinted(final boolean headerPrinted) {
            this.headerPrinted = headerPrinted;
        }

    }

    private void printItemHeaderLine(final String itemId,
                                     final String itemPath,
                                     final HeaderStatus headerStatus) {
        if (!headerStatus.isHeaderPrinted()) {
            System.out.printf("Problems with item %s (id: %s):\n", itemPath, itemId);
            headerStatus.setHeaderPrinted(true);
        }
    }

}
