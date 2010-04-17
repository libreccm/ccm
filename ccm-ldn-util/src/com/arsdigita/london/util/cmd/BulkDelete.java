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
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.domain.DomainObjectFactory;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemCollection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import java.math.BigDecimal;


public class BulkDelete extends BulkUnpublish {

    private static final Logger s_log = Logger.getLogger(BulkDelete.class);

    protected boolean deleteFolderStructure;

    public BulkDelete() {
        super("Bulk Delete", "1.0.0");

        Options options = getOptions();

        options.addOption(
            OptionBuilder
            .hasArg(false)
            .withLongOpt( "deletefolderstructure" )
            .withDescription( "Also delete empty folders (folders containing only folders)" )
            .create( "e" ) );

    }

    protected void doRun(CommandLine cmdLine) {
        //delete(new OID(ContentItem.BASE_DATA_OBJECT_TYPE, 13890351));

        super.ignoreErrors = cmdLine.hasOption("i");
        this.deleteFolderStructure = cmdLine.hasOption("e");

        if( cmdLine.hasOption( "t" ) ) {
            super.types = cmdLine.getOptionValues( "t" );

            System.out.println( "Deleting items of type:" );
            for( int i = 0; i < super.types.length; i++ ) {
                System.out.println( super.types[i] );
            }
        } else {
            super.types = null;
            System.out.println( "Deleting items without item type restriction" );
        }
        if (cmdLine.hasOption("f")) {
            super.folderId = Integer.parseInt(cmdLine.getOptionValue("f"));
            Folder folder = new Folder(new OID(Folder.BASE_DATA_OBJECT_TYPE, super.folderId));
            System.out.println( "Deleting items in folder: " + folder.getDisplayName());

            //Unpublish first
            List toProcess = getListToProcess(ContentPage.BASE_DATA_OBJECT_TYPE);
            unpublish(toProcess);
            //Delete
            toProcess = getListToProcess(ContentBundle.BASE_DATA_OBJECT_TYPE);
            delete(toProcess);

            if(deleteFolderStructure) {
                System.out.println("Deleting empty folders");
                deleteFolderStructure();
            }

        } else {
            System.err.println("No folder specified.");
        }

    }

    public static void main(String[] args) {
        new BulkDelete().run(args);
    }

    protected void delete(List toProcess) {
        final Iterator items = toProcess.iterator();
        while (items.hasNext()) {
            final OID oid = (OID) items.next();
            delete(oid);
        }
    }

    protected void delete(final OID oid) {
            final boolean ignoreErrors = super.ignoreErrors;

            Transaction txn = new Transaction() {
                      public void doRun() {
                          ContentItem item = (ContentItem)
                              DomainObjectFactory.newInstance(oid);
                              System.out.println("Deleting item " + oid + " " + item.getPath());
                          if (s_log.isInfoEnabled()) {
                              s_log.info("Deleting item " + oid + " " + item.getPath());
                          }
                          if(item instanceof ContentBundle) {
                              ContentBundle bundle = (ContentBundle) item;
                              ItemCollection instances = bundle.getInstances();
                              while (instances.next()) {
                                  instances.getContentItem().delete();
                              }
                          }
                          item.delete();
                      }
                  };
            try {
                txn.run();
            } catch (Throwable ex) {
                s_log.error("Cannot delete " + oid, ex);
                if (!ignoreErrors) {
                    return;
                }
            }
    }

    protected void deleteFolderStructure() {
        final Folder folder = new Folder(new OID(Folder.BASE_DATA_OBJECT_TYPE, super.folderId));

        Transaction txn = new Transaction() {
            public void doRun() {
                deleteFolderStructure(folder);
            }
        };
        try {
            txn.run(); 
        } catch (Throwable ex) {
            s_log.error("Error deleting folders ", ex);
            if (!ignoreErrors) {
                return;
            }
        }
    }

    protected void deleteFolderStructure(Folder folder) {
        final ItemCollection itemCollection = folder.getItems(true);

        while(itemCollection.next()) {
            ContentItem contentItem = itemCollection.getContentItem();

            if(contentItem instanceof Folder) {
                deleteFolderStructure((Folder) contentItem);
                if ( ((Folder) contentItem).isEmpty() ) {
                    contentItem.delete();
                }
            }
        }

    }
}
