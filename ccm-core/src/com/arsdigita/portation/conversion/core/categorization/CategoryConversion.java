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

import com.arsdigita.categorization.CategorizedCollection;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.portation.conversion.NgCollection;
import com.arsdigita.portation.modules.core.categorization.Categorization;
import com.arsdigita.portation.modules.core.categorization.Category;
import com.arsdigita.portation.modules.core.core.CcmObject;

import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created the 6/29/16
 */
public class CategoryConversion {

    public static void convertAll() {
        List<com.arsdigita.categorization.Category> trunkCategories = com
                .arsdigita.categorization.Category.getAllObjectCategories();

        // create categories
        trunkCategories.forEach(Category::new);

        setAssociations(trunkCategories);
    }

    /**
     * Sets associations. Needs to be separate, so that all categories have
     * been converted before. Otherwise it will be complex to get parent.
     *
     * @param trunkCategories
     */
    private static void setAssociations(
            List<com.arsdigita.categorization.Category> trunkCategories) {
        Category category, parentCategory;

        for (com.arsdigita.categorization.Category
                trunkCategory : trunkCategories) {
            category = NgCollection.categories.get(trunkCategory.getID()
                    .longValue());

            // set parent associations
            parentCategory = NgCollection.categories.get(trunkCategory
                    .getDefaultParentCategory().getID().longValue());
            setParentCategory(category, parentCategory);

            // create categorizations only for category typed objects
            CategorizedCollection categorizedCollection = trunkCategory
                    .getObjects(com.arsdigita.categorization.Category
                    .BASE_DATA_OBJECT_TYPE);
            createCategorizations(category, categorizedCollection);
        }
    }

    private static void setParentCategory(Category category, Category
            parentCategory) {
        if (category != null && parentCategory != null) {
            // set parent and opposed association
            category.setParentCategory(parentCategory);
            parentCategory.addSubCategory(category);
        }
    }

    private static void createCategorizations(Category category,
                                              CategorizedCollection
                                                      categorizedObjects) {
        CcmObject categorizedObject; Categorization categorization;

        while (categorizedObjects.next()) {
            categorizedObject = NgCollection.ccmObjects.get(((ACSObject)
                    categorizedObjects.getDomainObject()).getID().longValue());
            // create categorizations
            categorization = new Categorization(category,
                    categorizedObject);

            // set opposed associations
            category.addObject(categorization);
            categorizedObject.addCategory(categorization);
        }
    }
}
