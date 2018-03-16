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
package com.arsdigita.portation.modules.core.categorization.util;

import com.arsdigita.portation.modules.core.l10n.LocalizedString;

import java.util.Locale;

/**
 * Helper class for cms folder.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 3/2/18
 */
public class CategoryInformation {
    private String displayName;
    private String uniqueId;
    private String name;
    private LocalizedString title;
    private LocalizedString description;
    private boolean enabled;
    private boolean visible;
    private boolean abstractCategory;
    private long categoryOrder;

    public CategoryInformation(final String displayName,
                               final String uniqueId,
                               final String name,
                               final String title,
                               final String description,
                               final boolean enabled,
                               final boolean visible,
                               final boolean abstractCategory,
                               final long categoryOrder) {
        this.displayName = displayName;

        this.uniqueId = uniqueId;
        this.name = name;

        this.title = new LocalizedString();
        this.description = new LocalizedString();
        Locale locale = Locale.getDefault();
        this.title.addValue(locale, title);
        this.description.addValue(locale, description);

        this.enabled = enabled;
        this.visible = visible;
        this.abstractCategory = abstractCategory;

        this.categoryOrder = categoryOrder;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }

    public LocalizedString getTitle() {
        return title;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isAbstractCategory() {
        return abstractCategory;
    }

    public long getCategoryOrder() {
        return categoryOrder;
    }
}
