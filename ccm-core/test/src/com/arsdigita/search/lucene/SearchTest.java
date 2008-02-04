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
package com.arsdigita.search.lucene;

import com.arsdigita.search.Search;
import java.math.BigDecimal;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;

/**
 * SearchTest
 *
 * @author <a href="mailto:jorris@redhat.com">Jon Orris</a>
 *
 */
public class SearchTest extends LuceneTestCase {

    private static Logger s_log =
            Logger.getLogger(SearchTest.class);

    public SearchTest(String name) {
        super(name);
    }

    public void test() throws Exception {
        Note foo = new Note();
        foo.setName("Foo");
        foo.setText("This is a foo!");
        foo.save();
        BigDecimal fooID = foo.getID();

        Note bar = new Note();
        bar.setName("Bar");
        bar.setText("I am not Foo. I am Bar!");
        bar.save();
        BigDecimal barID = bar.getID();

        sync();

        LuceneSearch search = new LuceneSearch("Foo");
        int count = 0;
        boolean fooFound = false;
        boolean barFound = false;
        while(search.next()) {
            count++;
            BigDecimal id = search.getID();
            if (id.equals(fooID)) {
                fooFound = true;
            } else if(id.equals(bar.getID())) {
                barFound = true;
            } else {
                fail("Unknown object found! " + id + " " + search.getTitle());
            }
        }
        assertTrue("Foo not found!", fooFound);
        assertTrue("Bar not found!", barFound);
        assertTrue("Objects found multiple times: " + count, 2 == count);

        search = new LuceneSearch("Bar");
        if(search.next()) {
            BigDecimal id = search.getID();
            assertEquals("Didn't find bar???",barID, id);
        } else {
             fail("Bar not found");
        }

        if (search.next()) {
            fail("More than one object returned! " + search.getID() + " " + search.getTitle() );
        }

    }

    public void typeSearch() throws Exception {
        Note note = new Note();
        note.setName("TypeTest");
        note.setText("TypeTest");
        note.save();
        BigDecimal noteID = note.getID();

        Note other = new Note();
        other.setName("TypeTest");
        other.setText("TypeTest");
        other.save();
        BigDecimal otherID = other.getID();
        sync();

        LuceneSearch search = new LuceneSearch("TypeTest", Note.BASE_DATA_OBJECT_TYPE);
        if(search.next()) {
            BigDecimal id = search.getID();
            assertEquals("Didn't find note???",noteID, id);
            assertEquals("Types don't match!", Note.BASE_DATA_OBJECT_TYPE,search.getType());
        } else {
             fail("Note not found");
        }

        if (search.next()) {
            fail("More than one object returned! " + search.getID() + " " + search.getTitle() + " " + search.getType());
        }


        search = new LuceneSearch("TypeTest", OtherType.BASE_DATA_OBJECT_TYPE);
        if(search.next()) {
            BigDecimal id = search.getID();
            assertEquals("Didn't find Other???",otherID, id);
            assertEquals("Types don't match!", OtherType.BASE_DATA_OBJECT_TYPE,search.getType());
        } else {
             fail("Note not found");
        }

        if (search.next()) {
            fail("More than one object returned! " + search.getID() + " " + search.getTitle() + " " + search.getType());
        }

    }

    protected void setUp() throws Exception {
        super.setUp();
        if (Search.getConfig().isLuceneEnabled()) {
            cleanIndex();
            Registry reg = Registry.getInstance();
            reg.addAdapter(Note.BASE_DATA_OBJECT_TYPE, Note.adapter());

        }
    }

    protected void tearDown() throws Exception {
        if (Search.getConfig().isLuceneEnabled()) {
            cleanIndex();
            super.tearDown();
        }
    }

    private void sync() throws Exception {
        Indexer idx = new Indexer(Index.getLocation());
        idx.sync();

    }
    private void cleanIndex() throws Exception {
        LuceneConfig conf = LuceneConfig.getConfig();
        Analyzer analyzer = conf.getAnalyzer();
        synchronized (LuceneLock.getInstance()) {
            IndexWriter iw = new IndexWriter(Index.getLocation(),
                                             analyzer,
                                             true);
            iw.close();

        }

    }
}
