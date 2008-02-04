/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.docmgr.search;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;

public abstract class SearchUtils {

    private static final Logger s_log = Logger.getLogger(SearchUtils.class);

    //private static SearchJobQueue s_queue = new SearchJobQueue();
    private static Vector s_threads = new Vector();

    private static Searcher s_searcher;

    public static synchronized void setSearcher( Searcher searcher ) {
        s_searcher = searcher;
    }

    public static synchronized Searcher getSearcher() {
        return s_searcher;
    }


    //public static synchronized void setThreadCount( int nThreads ) {
    //    int runningThreads = s_threads.size();
    //
    //    if ( runningThreads > nThreads ) {
    //        Iterator threads = s_threads.iterator();
    //
    //        for ( int i = nThreads; i < runningThreads; i++ ) {
    //            RemoteSearcher searcher = (RemoteSearcher) threads.next();
    //            searcher.pleaseStop();
    //            threads.remove();
    //        }
    //    } else if ( runningThreads < nThreads ) {
    //
    //        for ( int i = runningThreads; i < nThreads; i++ ) {
    //            RemoteSearcher searcher = new RemoteSearcher( s_queue );
    //            searcher.start();
    //
    //            s_threads.add( searcher );
    //        }
    //    }
    //}
    //
    //
    //public static SearchJobQueue getJobQueue() {
    //    return s_queue;
    //}


    public static SearchResults getSimpleSearch(String terms, User user ) {
        s_log.debug("getSimpleSearch()");
        return getSearcher().simpleSearch(terms, user);
    }

    public static SearchResults getAdvancedSearch(String terms,
                                                  String author,
                                                  String mimeType,
                                                  BigDecimal workspaceID,
                                                  Date lastModifiedStartDate,
                                                  Date lastModifiedEndDate,
                                               String[] types,
                                               String[] sections,
                                               User user, 
                                               Collection categoryIDs) {
        return getSearcher().advancedSearch
            (terms, author,mimeType, workspaceID,
             lastModifiedStartDate, lastModifiedEndDate, 
             types, sections, user, categoryIDs);
    }

    public static void reindexObjects(DataCollection objects,
                                      String idAttribute) {
        while (objects.next()) {
            BigDecimal id = (BigDecimal)objects.get(idAttribute);
            
            try {
                s_log.debug( "Reindexing object id: " + id );
                ACSObject acsObject =
                    (ACSObject) DomainObjectFactory.newInstance
                    ( new OID( ACSObject.BASE_DATA_OBJECT_TYPE, id ) );
                acsObject.save();
                
                System.gc();
                Thread.currentThread().yield();
            } catch( DataObjectNotFoundException ex ) {
                s_log.warn( "Object " + id +
                            " in search content does not exist" );
            }
        }
    }
}
