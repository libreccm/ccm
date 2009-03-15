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

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.tools.junit.framework.BaseTestCase;

public class RegistryTest extends BaseTestCase {

    public RegistryTest(String name) {
        super(name);
    }

    public void testRegistry() throws Throwable {
        // Initial state, nothing should be found
        assertTrue(MetadataProviderRegistry.getAdapter(ACSObject.BASE_DATA_OBJECT_TYPE) == null);
        assertTrue(MetadataProviderRegistry.getAdapter(Note.BASE_DATA_OBJECT_TYPE) == null);
        assertTrue(MetadataProviderRegistry.findAdapter(Note.BASE_DATA_OBJECT_TYPE) == null);
        
        // Register bogus adapter for ACSObject
        NoteAdapter apt1 = new NoteAdapter();
        MetadataProviderRegistry.registerAdapter(ACSObject.BASE_DATA_OBJECT_TYPE,
                                                 apt1);
        assertTrue(MetadataProviderRegistry.getAdapter(ACSObject.BASE_DATA_OBJECT_TYPE) == apt1);
        assertTrue(MetadataProviderRegistry.getAdapter(Note.BASE_DATA_OBJECT_TYPE) == null);
        assertTrue(MetadataProviderRegistry.findAdapter(Note.BASE_DATA_OBJECT_TYPE) == apt1);
        
        // Register specific one for Note
        NoteAdapter apt2 = new NoteAdapter();
        MetadataProviderRegistry.registerAdapter(Note.BASE_DATA_OBJECT_TYPE,
                                                 apt2);
        assertTrue(MetadataProviderRegistry.getAdapter(ACSObject.BASE_DATA_OBJECT_TYPE) == apt1);
        assertTrue(MetadataProviderRegistry.getAdapter(Note.BASE_DATA_OBJECT_TYPE) == apt2);
        assertTrue(MetadataProviderRegistry.findAdapter(Note.BASE_DATA_OBJECT_TYPE) == apt2);
        
        // Remove the one for ACSObject
        MetadataProviderRegistry.unregisterAdapter(ACSObject.BASE_DATA_OBJECT_TYPE);
        assertTrue(MetadataProviderRegistry.getAdapter(ACSObject.BASE_DATA_OBJECT_TYPE) == null);
        assertTrue(MetadataProviderRegistry.getAdapter(Note.BASE_DATA_OBJECT_TYPE) == apt2);
        assertTrue(MetadataProviderRegistry.findAdapter(Note.BASE_DATA_OBJECT_TYPE) == apt2);
        
        // Remove the one for Note
        MetadataProviderRegistry.unregisterAdapter(Note.BASE_DATA_OBJECT_TYPE);
        assertTrue(MetadataProviderRegistry.getAdapter(ACSObject.BASE_DATA_OBJECT_TYPE) == null);
        assertTrue(MetadataProviderRegistry.getAdapter(Note.BASE_DATA_OBJECT_TYPE) == null);
        assertTrue(MetadataProviderRegistry.findAdapter(Note.BASE_DATA_OBJECT_TYPE) == null);
    }

    protected void tearDown() throws Exception {
        MetadataProviderRegistry.unregisterAdapter(Note.BASE_DATA_OBJECT_TYPE);
    }
}
