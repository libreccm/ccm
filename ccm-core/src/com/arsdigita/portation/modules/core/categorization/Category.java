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

import com.arsdigita.categorization.CategoryLocalization;
import com.arsdigita.categorization.CategoryLocalizationCollection;
import com.arsdigita.portation.Portable;
import com.arsdigita.portation.conversion.NgCoreCollection;
import com.arsdigita.portation.modules.core.categorization.util.CategoryInformation;
import com.arsdigita.portation.modules.core.core.CcmObject;
import com.arsdigita.portation.modules.core.l10n.LocalizedString;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
 * @version created on 6/15/16
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
                  resolver = CategoryIdResolver.class,
                  property = "uuid")
public class Category extends CcmObject implements Portable {

    private String uniqueId;
    private String name;
    private LocalizedString title;
    private LocalizedString description;
    private boolean enabled;
    private boolean visible;
    private boolean abstractCategory;
    @JsonIgnore
    private List<Categorization> objects;
    @JsonIgnore
    private List<Category> subCategories;
    @JsonIdentityReference(alwaysAsId = true)
    private Category parentCategory;
    private long categoryOrder;

    /**
     * Constructor for the ng-object.
     *
     * @param trunkCategory the trunk object
     */
    public Category(final com.arsdigita.categorization.Category trunkCategory) {
        super(trunkCategory);

        this.uniqueId = trunkCategory.getID().toString();
        this.name = trunkCategory.getName();

        this.title = new LocalizedString();
        this.description = new LocalizedString();
        CategoryLocalizationCollection categoryLocalizationCollection =
                trunkCategory.getCategoryLocalizationCollection();
        if (categoryLocalizationCollection != null &&
                categoryLocalizationCollection.next()) {

            CategoryLocalization categoryLocalization =
                    categoryLocalizationCollection.getCategoryLocalization();
            if (categoryLocalization != null) {
                Locale locale = new Locale(categoryLocalization.getLocale());
                this.title.addValue(locale, categoryLocalization.getName());
                this.description.addValue(locale, categoryLocalization.getDescription());
            }
        }

        this.enabled = trunkCategory.isEnabled();
        this.visible = trunkCategory.isVisible();
        this.abstractCategory = trunkCategory.isAbstract();

        this.objects = new ArrayList<>();
        this.subCategories = new ArrayList<>();

        //this.parentCategory

        com.arsdigita.categorization.Category defaultParent = null;
        try {
            defaultParent = trunkCategory.getDefaultParentCategory();
        } catch (Exception ignored) {}
        this.categoryOrder = defaultParent != null
                ? defaultParent.getNumberOfChildCategories() + 1
                : 0;

        NgCoreCollection.categories.put(this.getObjectId(), this);
    }

    /**
     * Specific constructor for subclasses of category e.g. Folder
     *
     * @param categoryInformation the trunk object
     */
    public Category(final CategoryInformation categoryInformation) {
        super(categoryInformation.getObjectId(),
                categoryInformation.getDisplayName());

        this.uniqueId = categoryInformation.getObjectId().toString();
        this.name = categoryInformation.getName();

        this.title = categoryInformation.getTitle();
        this.description = categoryInformation.getDescription();

        this.enabled = categoryInformation.isEnabled();
        this.visible = categoryInformation.isVisible();
        this.abstractCategory = categoryInformation.isAbstractCategory();

        this.objects = new ArrayList<>();
        this.subCategories = new ArrayList<>();

        //this.parentCategory

        this.categoryOrder = categoryInformation.getCategoryOrder();
    }


    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(final String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public LocalizedString getTitle() {
        return this.title;
    }

    public void setTitle(final LocalizedString title) {
        this.title = title;
    }

    public LocalizedString getDescription() {
        return this.description;
    }

    public void setDescription(final LocalizedString description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(final boolean visible) {
        this.visible = visible;
    }

    public boolean isAbstractCategory() {
        return abstractCategory;
    }

    public void setAbstractCategory(final boolean abstractCategory) {
        this.abstractCategory = abstractCategory;
    }

    public List<Categorization> getObjects() {
        return objects;
    }

    public void setObjects(final List<Categorization> objects) {
        this.objects = objects;
    }

    public void addObject(final Categorization object) {
        this.objects.add(object);
    }

    public void removeObject(final Categorization object) {
        this.objects.remove(object);
    }

    public List<Category> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(final List<Category> subCategories) {
        this.subCategories = subCategories;
    }

    public void addSubCategory(final Category subCategory) {
        this.subCategories.add(subCategory);
    }

    public void removeSubCategory(final Category subCategory) {
        this.subCategories.remove(subCategory);
    }

    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(final Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    public long getCategoryOrder() {
        return categoryOrder;
    }

    public void setCategoryOrder(final long categoryOrder) {
        this.categoryOrder = categoryOrder;
    }
}
