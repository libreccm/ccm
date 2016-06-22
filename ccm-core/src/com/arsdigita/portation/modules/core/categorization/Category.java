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
import com.arsdigita.portation.Identifiable;
import com.arsdigita.portation.modules.core.core.CcmObject;
import com.arsdigita.portation.modules.core.l10n.LocalizedString;

import java.util.List;

/**
 * The category entity represents a single category. Each category is part of a
 * {@code Domain}. A category can be assigned to multiple {@link CcmObject}s.
 *
 * In the old structure the properties of this class were split between the
 * {@code Category} entity from {@code ccm-core} and the {@code Term} entity
 * from {@code ccm-ldn-terms}. This class unifies the properties of these two
 * classes.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 6/15/16
 */
public class Category extends CcmObject {

    private String uniqueId;
    private String name;

    private LocalizedString title;
    private LocalizedString description;

    private boolean enabled;
    private boolean visible;
    private boolean abstractCategory;

    private List<Categorization> objects;

    private List<Category> subCategories;
    private Category parentCategory;
    private long categoryOrder;


    public Category(final com.arsdigita.categorization.Category trunkCategory) {
        super(trunkCategory);
    }


    @Override
    public AbstractMarshaller<? extends Identifiable> getMarshaller() {
        return new CategoryMarshaller();
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalizedString getTitle() {
        return this.title;
    }

    public void setTitle(LocalizedString title) {
        this.title = title;
    }

    public LocalizedString getDescription() {
        return this.description;
    }

    public void setDescription(LocalizedString description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isAbstractCategory() {
        return abstractCategory;
    }

    public void setAbstractCategory(boolean abstractCategory) {
        this.abstractCategory = abstractCategory;
    }

    public List<Categorization> getObjects() {
        return objects;
    }

    public void setObjects(List<Categorization> objects) {
        this.objects = objects;
    }

    public List<Category> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<Category> subCategories) {
        this.subCategories = subCategories;
    }

    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    public long getCategoryOrder() {
        return categoryOrder;
    }

    public void setCategoryOrder(long categoryOrder) {
        this.categoryOrder = categoryOrder;
    }
}
