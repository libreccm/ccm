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

import com.arsdigita.tools.junit.framework.BaseTestCase;

public class ObserverTest extends BaseTestCase {

    public ObserverTest(String name) {
        super(name);
    }

    public void testObservers() throws Throwable {
        TestSearchIndex.reset();
        
        Search.getConfig().setIndexerType(SearchSuite.TEST_INDEXER);
        Search.getConfig().setLazyUpdates(false);

        Note note1 = Note.create(
            "Note Number 1",
            "You can fool some of the people some of the time," +
            "and some of the people all of the time," +
            "and that is sufficient.");
        note1.save();

        // If no adapter is available, it shouldn't end up in
        // the index
        assertTrue(!TestSearchIndex.containsDocument(note1));
        
        // Add the note adapter
        MetadataProviderRegistry.registerAdapter(Note.BASE_DATA_OBJECT_TYPE,
                                                 new NoteAdapter());
        
        Note note2 = Note.create(
            "Note Number 2",
            "We took four laboratory mice, and for six days exposed " +
            "them to Mozart's 'Clarinet Quintet'.  After the six days " +
            "was over, we then placed an actual clarinet inside the " +
            "cage with the mice, to see if the mice had grasped the " +
            "subtle nuances of classical music.  The results... were " +
            "disappointing.  Next time, we will feed and water the mice."+
            "Now, Dave with a sideways look at deoxyribonucleic acid.  Dave");
        note2.save();
        assertTrue(TestSearchIndex.containsDocument(note2));
        
        // Check it disappears when deleted
        note2.delete();
        assertTrue(!TestSearchIndex.containsDocument(note2));
    }

    protected void tearDown() throws Exception {
        MetadataProviderRegistry.unregisterAdapter(Note.BASE_DATA_OBJECT_TYPE);
    }
}
