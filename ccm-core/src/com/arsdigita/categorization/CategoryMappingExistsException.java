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

import com.arsdigita.kernel.ACSObject;

/**
 *
 * This is thrown when an attempt is made to add a mapping that already exists.
 *
 * @author Randy Graebner
 * @version $Revision: #11 $ $DateTime: 2004/08/16 18:10:38 $
 */
public class CategoryMappingExistsException extends CategorizationException {
    private ACSObject acsObject = null;
    private Category category = null;

    public CategoryMappingExistsException(String message) {
        super(message);
    }


    public CategoryMappingExistsException(Exception e) {
        this(e.getMessage());
    }


    /**
     * Sets the offending object.
     */
    public void setObject(ACSObject object) {
        acsObject = object;
    }


    /**
     * Sets the parent category from the mapping.
     */
    public void setCategory(Category cat) {
        category = cat;
    }


    /**
     * Returns the object that caused this exception.
     */
    public ACSObject getObject() {
        return acsObject;
    }


    /**
     * Returns the category for which the exception was thrown.
     */
    public Category getCategory() {
        return category;
    }
}
