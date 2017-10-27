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
import com.arsdigita.portation.conversion.NgCoreCollection;
import com.arsdigita.portation.modules.core.categorization.Categorization;
import com.arsdigita.portation.modules.core.categorization.Category;
import com.arsdigita.portation.modules.core.core.CcmObject;

import java.util.ArrayList;
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
        System.err.printf("\tFetching categories from database...");
        List<com.arsdigita.categorization.Category> trunkCategories = com
                .arsdigita.categorization.Category.getAllObjectCategories();
        System.err.println("done.");

        System.err.printf("\tConverting categories and categorizations...\n");
        createCategoryAndCategorizations(trunkCategories);
        setRingAssociations(trunkCategories);
        System.err.printf("\tSorting categories...\n");
        sortCategoryMap();

        System.err.println("\tdone.\n");
    }

    /**
     * Creates the equivalent ng-class of the {@link Category} and restores
     * the associations to other classes.
     *
     * @param trunkCategories List of all
     *                        {@link com.arsdigita.categorization.Category}s
     *                        from this old trunk-system.
     */
    private static void createCategoryAndCategorizations(
            List<com.arsdigita.categorization.Category> trunkCategories) {
        int processedCategories = 0, processedCategorizations = 0;


        for (com.arsdigita.categorization.Category
                trunkCategory : trunkCategories) {

            // create categories
            Category category = new Category(trunkCategory);

            // create categorizations only for category typed objects
            CategorizedCollection categorizedCollection = trunkCategory
                    .getObjects(com.arsdigita.categorization
                            .Category.BASE_DATA_OBJECT_TYPE);
            processedCategorizations += createCategorizations(category,
                    categorizedCollection);

            processedCategories++;
        }
        System.err.printf("\t\tCreated %d categories and\n" +
                          "\t\tcreated %d categorizations.\n",
                          processedCategories, processedCategorizations);
    }

    /**
     * Method for creating {@link Categorization}s between {@link Category}s
     * and {@link CcmObject}s which is an association-class and has not been
     * existent in this old system.
     *
     * @param category The {@link Category}
     * @param categorizedObjects A collection of the {@code Categorization}s
     *                           as they are represented in this trunk-system
     *
     * @return Number of how many {@link Categorization}s have been processed.
     */
    private static long createCategorizations(Category category,
                                              CategorizedCollection
                                                      categorizedObjects) {
        int processed = 0;

        while (categorizedObjects.next()) {
            CcmObject categorizedObject = NgCoreCollection
                    .ccmObjects
                    .get(((ACSObject) categorizedObjects
                            .getDomainObject())
                            .getID()
                            .longValue());

            if (category != null && categorizedObject != null) {
                // create categorizations
                Categorization categorization = new Categorization(category,
                        categorizedObject);

                // set opposed associations
                category.addObject(categorization);
                categorizedObject.addCategory(categorization);

                processed++;
            }
        }
        return processed;
    }

    /**
     * Method for setting the parent {@link Category} on the one side and the
     * sub-{@link Category}s on the other side.
     *
     * @param trunkCategories List of all
     *                        {@link com.arsdigita.categorization.Category}s
     *                        from this old trunk-system.
     */
    private static void setRingAssociations(
            List<com.arsdigita.categorization.Category> trunkCategories) {
        for (com.arsdigita.categorization.Category
                trunkCategory : trunkCategories) {

            Category category = NgCoreCollection
                    .categories
                    .get(trunkCategory.getID().longValue());

            // set parent and opposed association
            Category parentCategory = null;
            try {
                com.arsdigita.categorization.Category defaultParent =
                        trunkCategory.getDefaultParentCategory();

                if (defaultParent != null) {
                    parentCategory = NgCoreCollection
                            .categories
                            .get(
                            defaultParent.getID().longValue());
                }
            } catch (Exception ignored) {}
            if (category != null && parentCategory != null) {
                category.setParentCategory(parentCategory);
                parentCategory.addSubCategory(category);
            }
        }
    }

    /**
     * Sorts values of category-map to ensure that the parent-categories will
     * be listed before their childs in the export file.
     *
     * Runs once over the unsorted map and iterates over each their parents
     * to add them to the sorted list.
     */
    private static void sortCategoryMap() {
        ArrayList<Category> sortedList = new ArrayList<>();

        int runs = 0;
        for (Category category : NgCoreCollection.categories.values()) {
            addParent(sortedList, category);

            if (!sortedList.contains(category))
                sortedList.add(category);

            runs++;
        }
        NgCoreCollection.sortedCategories = sortedList;

        System.err.printf("\t\tSorted categories in %d runs.\n", runs);
    }

    /**
     * Recursively adds the parents of the given category to the sorted list
     * to guaranty that the parents will be imported before their childs.
     *
     * @param sortedList Map of already sorted categories
     * @param category Current category
     */
    private static void addParent(ArrayList<Category> sortedList, Category
            category) {
        Category parent = category.getParentCategory();

        if (parent != null) {
            addParent(sortedList, parent);

            if (!sortedList.contains(parent))
                sortedList.add(parent);
        }
    }
}
