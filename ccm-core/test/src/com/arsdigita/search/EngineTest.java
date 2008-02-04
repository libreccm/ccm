/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.search;

import com.arsdigita.search.filters.ObjectTypeFilterType;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import java.util.Iterator;
import org.apache.log4j.Logger;

public class EngineTest extends BaseTestCase {

    private static Logger s_log =
            Logger.getLogger(EngineTest.class);

    public EngineTest(String name) {
        super(name);

        
    }
    
    
    // This tests the EMPTY_RESULT_SET impl
    public void testNoopEngine() throws Throwable {
        TestSearchIndex.reset();
        Search.getConfig().setIndexerType(SearchSuite.TEST_INDEXER);
        Search.getConfig().setLazyUpdates(false);
        MetadataProviderRegistry.registerAdapter(Note.BASE_DATA_OBJECT_TYPE,
                                                 new NoteAdapter());

        QueryEngineRegistry.registerEngine(SearchSuite.TEST_INDEXER,
                                           new FilterType[] {},
                                           new NoopQueryEngine());
        
        QuerySpecification spec = new QuerySpecification("test",
                                                         false);
        
        ResultSet results = Search.process(spec,
                                           Search.NOP_RESULT_CACHE);
        assertTrue(results.getCount() == 0);

        Iterator i = results.getDocuments(0, 10);
        assertTrue(!i.hasNext());

        MetadataProviderRegistry.unregisterAdapter(Note.BASE_DATA_OBJECT_TYPE);
    }
    
    // This tests the simple test result set
    // and caching result set.
    public void testSimpleEngine() throws Throwable {
        TestSearchIndex.reset();
        Search.getConfig().setIndexerType(SearchSuite.TEST_INDEXER);
        Search.getConfig().setLazyUpdates(false);
        MetadataProviderRegistry.registerAdapter(Note.BASE_DATA_OBJECT_TYPE,
                                                 new NoteAdapter());

        for (int i = 0 ; i < 15 ; i++) {
            Note note1 = Note.create(
                "Note Number 1-" + i,
                "You can fool some of the people some of the time," +
                "and some of the people all of the time," +
                "and that is sufficient.");
            note1.save();
            Note note2 = Note.create(
                "Note Number 2-" + i,
                "We took four laboratory mice, and for six days exposed " +
                "them to Mozart's 'Clarinet Quintet'.  After the six days " +
                "was over, we then placed an actual clarinet inside the " +
                "cage with the mice, to see if the mice had grasped the " +
                "subtle nuances of classical music.  The results... were " +
                "disappointing.  Next time, we will feed and water the mice."+
                "Now, Dave with a sideways look at deoxyribonucleic acid.  Dave");
            note2.save();
        }
        
        QueryEngineRegistry.registerEngine(SearchSuite.TEST_INDEXER,
                                           new FilterType[] {},
                                           new TestQueryEngine());
        
        QueryEngineRegistry.registerEngine(SearchSuite.TEST_INDEXER,
                                           new FilterType[] {
                                               new ObjectTypeFilterType()
                                           },
                                           new TestQueryEngine());
        
        QuerySpecification spec = new QuerySpecification("fool",
                                                         false);
        
        s_log.debug("Testing with no-op cache");
        // First test without caching just to verify all is
        // working OK.
        ResultSet results = Search.process(spec,
                                           Search.NOP_RESULT_CACHE);
        assertTrue(results.getCount() == 15);

        Iterator i = results.getDocuments(0, 10);
        int count = 0;
        while (i.hasNext()) {
            count++;
            i.next();
        }
        assertTrue(count == 10);
        
        i = results.getDocuments(10, 10);
        count = 0;
        while (i.hasNext()) {
            count++;
            i.next();
        }
        assertTrue(count == 5);
        
        
        s_log.debug("Testing with default cache");
        // Now test same thing, but with caching of default
        // size & lifetime.
        results = Search.process(spec);
        assertTrue(TestSearchIndex.getSearchCount() == 2);
        assertTrue(results.getCount() == 15);

        i = results.getDocuments(0, 10);
        count = 0;
        while (i.hasNext()) {
            count++;
            i.next();
        }
        assertTrue(count == 10);
        
        i = results.getDocuments(10, 10);
        count = 0;
        while (i.hasNext()) {
            count++;
            i.next();
        }
        assertTrue(count == 5);
        
        s_log.debug("Testing with default cache again");
        // Lets do it again
        results = Search.process(spec);
        assertTrue(TestSearchIndex.getSearchCount() == 2);
        assertTrue(results.getCount() == 15);

        i = results.getDocuments(0, 10);
        count = 0;
        while (i.hasNext()) {
            count++;
            i.next();
        }
        assertTrue(count == 10);
        
        i = results.getDocuments(10, 10);
        count = 0;
        while (i.hasNext()) {
            count++;
            i.next();
        }
        assertTrue(count == 5);
        
        
        s_log.debug("Testing with default cache & bigger hits");
        // Now same, but with bigger result set.
        spec = new QuerySpecification("the",
                                      false);
        
        results = Search.process(spec, Search.NOP_RESULT_CACHE);
        assertTrue(TestSearchIndex.getSearchCount() == 3);
        assertTrue(results.getCount() == 30);

        i = results.getDocuments(0, 10);
        count = 0;
        while (i.hasNext()) {
            count++;
            i.next();
        }
        assertTrue(count == 10);
        
        i = results.getDocuments(10, 10);
        count = 0;
        while (i.hasNext()) {
            count++;
            i.next();
        }
        assertTrue(count == 10);
        
        i = results.getDocuments(20, 10);
        count = 0;
        while (i.hasNext()) {
            count++;
            i.next();
        }
        assertTrue(count == 10);
        
        
        s_log.debug("Testing doc count limits");
        // Now lets see how different cache doc count affets
        // stuff
        TimedResultCache sizeCache = new TimedResultCache(20, 
                                                          10, 
                                                          1000*60*5);

        // Now same with caching of first 20 rows
        results = Search.process(spec, sizeCache);
        assertTrue(TestSearchIndex.getSearchCount() == 4);
        assertTrue(results.getCount() == 30);

        // Should hit the cache 
        i = results.getDocuments(0, 10);
        assertTrue(TestSearchIndex.getSearchCount() == 4);
        count = 0;
        while (i.hasNext()) {
            count++;
            i.next();
        }
        assertTrue(count == 10);
        
        // Should still hit the cache 
        i = results.getDocuments(10, 10);
        assertTrue(TestSearchIndex.getSearchCount() == 4);
        count = 0;
        while (i.hasNext()) {
            count++;
            i.next();
        }
        assertTrue(count == 10);
        
        // Since we're only caching 20 results, this should
        // cause a new search to occurr
        i = results.getDocuments(20, 10);
        assertTrue(TestSearchIndex.getSearchCount() == 5);
        count = 0;
        while (i.hasNext()) {
            count++;
            i.next();
        }
        assertTrue(count == 10);
        
        s_log.debug("Testing doc count again");
        // Once more for luck
        results = Search.process(spec, sizeCache);
        assertTrue(TestSearchIndex.getSearchCount() == 5);
        assertTrue(results.getCount() == 30);

        // Should hit the cache 
        i = results.getDocuments(0, 10);
        assertTrue(TestSearchIndex.getSearchCount() == 5);
        count = 0;
        while (i.hasNext()) {
            count++;
            i.next();
        }
        assertTrue(count == 10);
        
        // Should still hit the cache 
        i = results.getDocuments(10, 10);
        assertTrue(TestSearchIndex.getSearchCount() == 5);
        count = 0;
        while (i.hasNext()) {
            count++;
            i.next();
        }
        assertTrue(count == 10);
        
        // Since we're only caching 20 results, this should
        // cause a new search to occurr
        i = results.getDocuments(20, 10);
        assertTrue(TestSearchIndex.getSearchCount() == 6);
        count = 0;
        while (i.hasNext()) {
            count++;
            i.next();
        }
        assertTrue(count == 10);
        
        
        s_log.debug("Testing time outs");
        // Finally test cache timeout
        TimedResultCache timeCache = new TimedResultCache(100, 
                                                          10, 
                                                          1000*5);
        
        spec = new QuerySpecification("mice",
                                      false);
        
        // First test with caching for 5 seconds
        results = Search.process(spec, timeCache);
        assertTrue(TestSearchIndex.getSearchCount() == 7);        
        assertTrue(results.getCount() == 15);

        s_log.debug("Testing time outs again");
        // should still hit cache
        results = Search.process(spec, timeCache);
        assertTrue(TestSearchIndex.getSearchCount() == 7);
        assertTrue(results.getCount() == 15);
        
        // Sleep so cache expires
        s_log.debug("Sleeping for a short while");
        Thread.sleep(10 * 1000l);
        results = Search.process(spec, timeCache);
        assertTrue(TestSearchIndex.getSearchCount() == 8);
        assertTrue(results.getCount() == 15);
        
    }
}
