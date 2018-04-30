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
package com.arsdigita.cms.portation.conversion.contentsection;

import com.arsdigita.cms.portation.modules.contentsection.Folder;
import com.arsdigita.cms.portation.modules.contentsection.FolderType;
import com.arsdigita.portation.AbstractConversion;
import com.arsdigita.portation.cmd.ExportLogger;

import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 4/16/18
 */
public class FolderConversion extends AbstractConversion {
    private static FolderConversion instance;

    static {
        instance = new FolderConversion();
    }

    /**
     * Retrieves all trunk-{@link com.arsdigita.cms.Folder}s from the
     * persistent storage and collects them in a list. Then calls for
     * creating the equivalent ng-{@link Folder}s focusing on keeping all the
     * associations in tact.
     */
    @Override
    public void convertAll() {
        ExportLogger.fetching("folders");
        List<com.arsdigita.cms.Folder> trunkFolders = com.arsdigita.cms
                .Folder.getAllObjects();

        ExportLogger.converting("folders");
        createFoldersAndSetAssociations(trunkFolders);

        ExportLogger.newLine();
    }

    /**
     * Creates the equivalent ng-class of the {@code Folder} and restores
     * the associations to other classes.
     *
     * @param trunkFolders List of all {@link com.arsdigita.cms.Folder}s from
     *                     this old trunk-system.
     */
    private void createFoldersAndSetAssociations(final List<com.arsdigita.cms
            .Folder> trunkFolders) {
        int processed = 0;
        for (com.arsdigita.cms.Folder trunkFolder : trunkFolders) {

            // create folder
            Folder folder = new Folder(trunkFolder);

            // set section
            // -> will be done somewhere else

            // set type -> to DOCUMENT_FOLDER
            folder.setType(FolderType.DOCUMENTS_FOLDER);

            processed++;
        }
        ExportLogger.created("folders", processed);
    }

    public static FolderConversion getInstance() {
        return instance;
    }
}
