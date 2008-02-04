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
package com.arsdigita.categorization;

import com.arsdigita.kernel.Group;
import java.util.Collection;
import org.apache.log4j.Logger;

/**
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2004-01-23
 * @version $Revision: #5 $ $DateTime: 2004/08/16 18:10:38 $
 */
public class CategorizedObjectTest extends CategoryTestCase {

    private final static Logger s_log = Logger.getLogger(CategorizedObjectTest.class);

    Category cat1;
    Category cat2;
    private Group group;

    public CategorizedObjectTest( String name ) {
        super( name );
    }

    public void setUp() {
        cat1 = new Category();
        cat1.setName("child");
        cat2 = new Category();
        cat2.setName("parent");
        group = new Group();
        group.setName("group");
    }

    public void testGetParentCategories() {
        cat2.addChild(cat1);
        cat1.addChild(group);
        CategorizedObject catObj = new CategorizedObject(group);
        Collection parents = catObj.getParentCategories();
        assertEquals("single parent", 1, parents.size());
        assertTrue("cat1 is the only parent", parents.contains(cat1));
    }

    public void testGetParents() {
        cat2.addChild(cat1);
        cat1.addChild(group);
        CategorizedObject catObj = new CategorizedObject(group);

        assertSize(catObj.getParents(), 1);
        assertContains(catObj.getParents(), cat1);
    }
}
