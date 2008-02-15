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

import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.Folder;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class BulkUnpublish extends Program {

    private static final Logger s_log = Logger.getLogger(BulkUnpublish.class);

    public BulkUnpublish() {
        super("Bulk Unpublish",
              "1.0.0",
              "");

        Options options = getOptions();

        options.addOption(
            OptionBuilder
            .hasArgs()
            .withLongOpt( "types" )
            .withDescription( "Restrict unpublishing to items of the specified content types" )
            .create( "t" ) );
        options.addOption(
  			  OptionBuilder
  			  .hasArg()
  			  .withLongOpt( "restrictToFolderId" )
  			  .withDescription( "Restrict publishing to items within the folder with the specified id" )
  			  .create( "f" ) );
        
        options.addOption
            (OptionBuilder
             .hasArg(false)
             .withLongOpt("ignore-errors")
             .withDescription("Ignore any errors")
             .create('i'));
    }

    protected void doRun(CommandLine cmdLine) {
    	final int folderId;
        final String[] types;
        final boolean ignoreErrors = cmdLine.hasOption("i");

        if( cmdLine.hasOption( "t" ) ) {
            types = cmdLine.getOptionValues( "t" );

            System.out.println( "Unpublishing live items of types:" );
            for( int i = 0; i < types.length; i++ ) {
                System.out.println( types[i] );
            }
        } else {
            types = null;
            System.out.println( "Unpublishing all live items" );
        }
        if (cmdLine.hasOption("f")) {
            folderId = Integer.parseInt(cmdLine.getOptionValue("f"));
            Folder folder = new Folder(new OID(Folder.BASE_DATA_OBJECT_TYPE, folderId));
            	System.out.println( "Unpublishing items in folder: " + folder.getDisplayName());
        } else {
        	folderId = -1;
        }

        final List toUnpublish = new ArrayList();
        new Transaction() {
            public void doRun() {
                DataCollection items = SessionManager.getSession()
                    .retrieve(ContentPage.BASE_DATA_OBJECT_TYPE);
                items.addNotEqualsFilter("type.id", null);
                items.addEqualsFilter("version", ContentItem.LIVE);
                items.addOrder("title");

                FilterFactory filterFactory = items.getFilterFactory();

                if (folderId >= 0) {
        		    Filter filter = filterFactory.simple(" ancestors like '%/" + folderId + "/%'");
        		    items.addFilter(filter);
        		}
                if( null != types ) {
                    CompoundFilter or = filterFactory.or();

                    for( int i = 0; i < types.length; i++ ) {
                        or.addFilter( filterFactory.equals( "objectType", types[i] ) );
                    }

                    items.addFilter( or );
                }

                while (items.next()) {
                    ContentPage page = (ContentPage) DomainObjectFactory.newInstance(items.getDataObject());
                    toUnpublish.add(page.getDraftVersion().getOID());
                }
            }
        }.run();

        final Iterator items = toUnpublish.iterator();
        while (items.hasNext()) {
            final OID oid = (OID) items.next();
            Transaction txn = new Transaction() {
                      public void doRun() {
                          ContentPage item = (ContentPage)
                              DomainObjectFactory.newInstance(oid);
                          if (s_log.isInfoEnabled()) {
                              s_log.info("Processing item " + oid + " " + item.getPath());
                          }
                          item.setLive(null);
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

    public static void main(String[] args) {
        new BulkUnpublish().run(args);
    }

}
