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

import com.arsdigita.cms.portation.conversion.NgCmsCollection;
import com.arsdigita.kernel.ACSObject;
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
     * Constructor for the ng-object, if old trunk object was retrieved from
     * systems database storage.
     *
     * => use to convert folders
     *
     * @param trunkFolder the trunk object
     */
    public Folder(final com.arsdigita.cms.Folder trunkFolder) {
        super(new CategoryInformation(
                trunkFolder.getID(),
                trunkFolder.getDisplayName(),
                trunkFolder.getName(),
                trunkFolder.getLabel(),
                trunkFolder.getAdditionalInfo(),
                true,
                true,
                false,
                0));

        //this.section
        //this.type

        NgCmsCollection.folders.put(this.getObjectId(), this);
    }

    /**
     * Constructor for the ng-object, if old trunk object did not exist and a
     * NEW one needed to be created. e.g. a new root folder for a content
     * section.
     *
     * => use to create new folders for a content section with selected a
     * {@link FolderType}
     *
     * @param folderType The type of the new folder
     * @param sectionName The name of the section
     */
    public Folder(final FolderType folderType, final String sectionName) {
        super(new CategoryInformation(
                ACSObject.generateID(),
                sectionName + "-" + folderType,
                sectionName + "-" + folderType,
                sectionName + "-" + folderType,
                String.format("This is the %s for the content section: %s.",
                        folderType, sectionName),
                true,
                true,
                false,
                0));

        //this.section
        this.type = folderType;

        NgCmsCollection.folders.put(this.getObjectId(), this);
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
