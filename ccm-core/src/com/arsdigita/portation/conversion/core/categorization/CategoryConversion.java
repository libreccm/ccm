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
 * Class for converting all
 * trunk-{@link com.arsdigita.categorization.Category}s into
 * ng-{@link Category}s as preparation for a successful export of all trunk
 * classes into the new ng-system.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created on 29.6.16
 */
public class CategoryConversion {

    /**
     * Retrieves all trunk-{@link com.arsdigita.categorization.Category}s from
     * the persistent storage and collects them in a list. Then calls for
     * creating the equivalent ng-{@link Category}s focusing on keeping all the
     * associations in tact.
     */
    public static void convertAll() {
        List<com.arsdigita.categorization.Category> trunkCategories = com
                .arsdigita.categorization.Category.getAllObjectCategories();

        createCategoryAndSetAssociations(trunkCategories);
    }

    /**
     * Creates the equivalent ng-class of the {@code Category} and restores
     * the associations to other classes.
     *
     * @param trunkCategories List of all
     *                        {@link com.arsdigita.categorization.Category}s
     *                        from this old trunk-system.
     */
    private static void createCategoryAndSetAssociations(
            List<com.arsdigita.categorization.Category> trunkCategories) {
        for (com.arsdigita.categorization.Category
                trunkCategory : trunkCategories) {
            // create categories
            Category category = new Category(trunkCategory);


            // set parent and opposed association
            Category parentCategory = null;
            try {
                com.arsdigita.categorization.Category defaultParent =
                        trunkCategory.getDefaultParentCategory();

                if (defaultParent != null) {
                    parentCategory = NgCollection.categories.get(
                            defaultParent.getID().longValue());
                }
            } catch (Exception e) {}
            if (parentCategory != null) {
                category.setParentCategory(parentCategory);
                parentCategory.addSubCategory(category);
            }

            // categorizations only for category typed objects
            CategorizedCollection categorizedCollection = trunkCategory
                    .getObjects(com.arsdigita.categorization.Category
                    .BASE_DATA_OBJECT_TYPE);
            createCategorizations(category, categorizedCollection);
        }
    }

    /**
     * Method for creating {@link Categorization}s between {@link Category}s
     * and {@link CcmObject}s which is an association-class and has not been
     * existent in this old system.
     *
     * @param category The {@link Category}
     * @param categorizedObjects A collection of the {@code Categorization}s
     *                           as they are represented in this trunk-system
     */
    private static void createCategorizations(Category category,
                                              CategorizedCollection
                                                      categorizedObjects) {
        while (categorizedObjects.next()) {
            CcmObject categorizedObject = NgCollection.ccmObjects.get(((ACSObject)
                    categorizedObjects.getDomainObject()).getID().longValue());

            if (category != null && categorizedObject != null) {
                // create categorizations
                Categorization categorization = new Categorization(category,
                        categorizedObject);

                // set opposed associations
                category.addObject(categorization);
                categorizedObject.addCategory(categorization);
            }
        }
    }
}
