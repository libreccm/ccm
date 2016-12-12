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
package com.arsdigita.portation.modules.core.categorization;

import com.arsdigita.portation.AbstractMarshaller;
import com.arsdigita.portation.Portable;
import com.arsdigita.portation.conversion.NgCollection;
import com.arsdigita.portation.modules.core.core.CcmObject;
import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * Association class describing the association between a category and an
 * object. Instances of these class should not created manually. The methods
 * provided by the {@code CategoryManager} take care of that.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created on 6/15/16
 */
public class Categorization implements Portable {

    private long categorizationId;

    @JsonManagedReference
    private Category category;
    @JsonManagedReference
    private CcmObject categorizedObject;

    private boolean index;
    private long categoryOrder;
    private long objectOrder;

    private String type;

    public Categorization(final Category category, final CcmObject
            categorizedObject) {
        this.categorizationId = NgCollection.categorizations.size() + 1;

        this.category = category;
        this.categorizedObject = categorizedObject;

        this.index = false;
        this.categoryOrder = categorizedObject.getCategories().size() + 1;
        this.objectOrder = category.getObjects().size() + 1;

        this.type = "";

        NgCollection.categorizations.put(this.categorizationId, this);
    }


    @Override
    public AbstractMarshaller<? extends Portable> getMarshaller() {
        return new CategorizationMarshaller();
    }

    public long getCategorizationId() {
        return categorizationId;
    }

    public void setCategorizationId(long categorizationId) {
        this.categorizationId = categorizationId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public CcmObject getCategorizedObject() {
        return categorizedObject;
    }

    public void setCategorizedObject(final CcmObject categorizedObject) {
        this.categorizedObject = categorizedObject;
    }

    public boolean isIndex() {
        return index;
    }

    public void setIndex(final boolean index) {
        this.index = index;
    }

    public long getCategoryOrder() {
        return categoryOrder;
    }

    public void setCategoryOrder(final long categoryOrder) {
        this.categoryOrder = categoryOrder;
    }

    public long getObjectOrder() {
        return objectOrder;
    }

    public void setObjectOrder(final long objectOrder) {
        this.objectOrder = objectOrder;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
