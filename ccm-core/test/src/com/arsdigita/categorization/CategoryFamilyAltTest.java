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
 *
 */
package com.arsdigita.categorization;

/**
 * This is an CategoryFamilyTest where Category has been created
 * with an alternate constructor.
 *
 *
 * @author David Eison
 * @version 1.0
 * @see com.arsdigita.categorization
 */
public class CategoryFamilyAltTest extends CategoryFamilyTest {


    public CategoryFamilyAltTest( String name ) {
        super( name );
    }


    /**
     * Runs tests on a category created with the OID constructor
     * (e.g. loaded from the DB rather than created from scratch).
     */
    protected void setupMainCategory() {
        super.setupMainCategory();
        cat = new Category(cat.getOID());
        cat.save();
    }
}
