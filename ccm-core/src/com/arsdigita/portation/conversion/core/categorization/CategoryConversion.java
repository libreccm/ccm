/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.portation.conversion.core.categorization;

import com.arsdigita.portation.conversion.NgCollection;
import com.arsdigita.portation.modules.core.categorization.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created the 6/29/16
 */
public class CategoryConversion {

    public static void convertAll() {
        // Todo:
        List<com.arsdigita.categorization.Category> trunkCategories = new ArrayList<>();

        trunkCategories.forEach(Category::new);

        setParentCategory(trunkCategories);
    }

    private static void setParentCategory(
            List<com.arsdigita.categorization.Category> trunkCategories) {
        Long id, parentId;
        Category category, parentCategory;

        for (com.arsdigita.categorization.Category
                trunkCategory : trunkCategories) {
            id = trunkCategory.getID().longValue();
            parentId = trunkCategory.getDefaultParentCategory().getID()
                    .longValue();

            category = NgCollection.categories.get(id);
            parentCategory = NgCollection.categories.get(parentId);

            if (category != null && parentCategory != null) {
                category.setParentCategory(parentCategory);
                parentCategory.addSubCategory(category);
            }
        }
    }
}
