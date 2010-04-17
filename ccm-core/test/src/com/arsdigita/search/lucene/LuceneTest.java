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

import org.apache.log4j.Logger;

/**
 * LuceneTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Id: LuceneTest.java 1708 2008-07-12 03:08:52Z terry $
 **/

public class LuceneTest extends LuceneTestCase {

    private static final Logger s_log = Logger.getLogger(LuceneTest.class);
    private static final String NAME =
        "This is a note.";
    private static final String TEXT =
        "This is the test text included in the note.";

    public LuceneTest(String name) {
        super(name);
    }

    public void testStandardAdapter() {
        Adapter ad = Note.adapter();

        Note note = new Note();
        note.setName(NAME);
        note.setText(TEXT);

        assertEquals(note.getID(), ad.getID(note));
        assertEquals(note.getObjectType(), ad.getObjectType(note));
        assertEquals(note.getName(), ad.getTitle(note));
        assertEquals(note.getText(), ad.getContent(note));
    }

    public void testObserver() throws Exception {
        Registry reg = Registry.getInstance();
        reg.addAdapter(Note.BASE_DATA_OBJECT_TYPE, Note.adapter());

        Note note = new Note();
        note.setName(NAME);
        note.setText(TEXT);
        note.save();

        Document doc = Document.retrieve(note.getID());
        assertEquals(NAME, doc.getTitle());
        assertEquals(TEXT, doc.getContent());
    }

    public void testIndexer() throws java.io.IOException {
        Registry reg = Registry.getInstance();
        reg.addAdapter(Note.BASE_DATA_OBJECT_TYPE, Note.adapter());

        Note note = new Note();
        note.setName(NAME);
        note.setText(TEXT + " " + note.getID());
        note.save();

        Indexer idx = new Indexer(Index.getLocation());
        idx.run();

        // This is kindof a hack since it's possible there are other things in
        // the index that will contain the same search term since the search
        // index isn't cleared between test runnings. I should really add ID
        // based search in order to do this right.
        LuceneSearch search = new LuceneSearch(note.getID().toString());
        if (search.next()) {
            assertEquals(note.getID(), search.getID());
            assertEquals(note.getName(), search.getTitle());
            assertEquals(note.getText(), search.getContent());
        } else {
            fail("Search or indexer failed.");
        }
    }


}
