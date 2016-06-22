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
package com.arsdigita.portation.modules.core.categorization.utils;

import com.arsdigita.categorization.CategorizedCollection;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.portation.modules.core.categorization.Categorization;
import com.arsdigita.portation.modules.core.categorization.Category;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created the 6/22/16
 */
public class CollectionConverter {

    public static final Logger logger = Logger.getLogger(CollectionConverter.class);

    /**
     * Converts CategorizationCollection of Trunk into a list of
     * Categorizations of CCM_NG.
     *
     * @param categorizedCollection
     * @param category
     * @return
     */
    public static List<Categorization> convertCategorizations
            (CategorizedCollection categorizedCollection, Category category) {
        List<Categorization> categorizations = new ArrayList<>();
        if (categorizedCollection != null) {
            while (categorizedCollection.next()) {
                //categorizations.add();
            }
            categorizedCollection.close();
        } else {
            logger.error("Failed to convertCategories, cause " +
                    "categoryCollection is null.");
        }
        return categorizations;
    }

    /**
     * Converts CategoryCollection of Trunk into list of Categories of CCM_NG.
     *
     * @param categoryCollection
     * @return
     */
    public static List<Category> convertCategories(CategoryCollection
                                                           categoryCollection) {
        List<Category> categories = new ArrayList<>();
        if (categoryCollection != null) {
            while (categoryCollection.next()) {
                //.add(new Category());
            }
            categoryCollection.close();
        } else {
            logger.error("Failed to convertCategories, cause " +
                    "categoryCollection is null.");
        }
        return categories;
    }
}
