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

package com.arsdigita.london.navigation.ui.admin;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.categorization.Category;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;


/**
 * A single selection model for a
 * {@link com.arsdigita.categorization.Category}.
 *
 * @author Stanislav Freidin (stas@arsdigita.com)
 * @version $Revision: #1 $ $DateTime: 2003/01/03 10:50:48 $
 */
public class CategorySelectionModel extends ACSObjectSelectionModel {

    public final static String CURRENT_CATEGORY = "category";

    public CategorySelectionModel() {
        super(Category.BASE_DATA_OBJECT_TYPE,
              "com.arsdigita.categorization.Category",
              new BigDecimalParameter(CURRENT_CATEGORY));
    }
    
    public Category getSelectedCategory(PageState state) {
        return (Category)getSelectedObject(state);
    }
}
