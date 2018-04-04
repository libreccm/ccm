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
package com.arsdigita.cms.portation.modules.contentsection;

import com.arsdigita.portation.modules.core.categorization.Category;
import com.arsdigita.portation.modules.core.categorization.util.CategoryInformation;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 3/2/18
 */
public class Folder extends Category {
    @JsonIgnore
    private ContentSection section;
    private FolderType type;

    /**
     * Constructor for the ng-object.
     *
     * @param trunkFolder the trunk object
     */
    public Folder(final com.arsdigita.cms.Folder trunkFolder) {
        super(new CategoryInformation(
                trunkFolder.getDisplayName(),
                trunkFolder.getID().toString(),
                trunkFolder.getName(),
                trunkFolder.getLabel(),
                trunkFolder.getAdditionalInfo(),
                true,
                true,
                false,
                0)
        );
        this.type = FolderType.DOCUMENTS_FOLDER;

        //this.section
        //this.type
    }

    public ContentSection getSection() {
        return section;
    }

    public void setSection(final ContentSection section) {
        this.section = section;
    }

    public FolderType getType() {
        return type;
    }

    public void setType(final FolderType type) {
        this.type = type;
    }
}
