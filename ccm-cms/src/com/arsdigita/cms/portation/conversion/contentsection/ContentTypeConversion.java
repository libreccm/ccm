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

import com.arsdigita.cms.portation.modules.contentsection.ContentType;
import com.arsdigita.portation.AbstractConversion;
import com.arsdigita.portation.cmd.ExportLogger;

import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 4/16/18
 */
public class ContentTypeConversion extends AbstractConversion {
    private static ContentTypeConversion instance;

    static {
        instance = new ContentTypeConversion();
    }

    /**
     * Retrieves all trunk-{@link com.arsdigita.cms.ContentType}s from the
     * persistent storage and collects them in a list. Then calls for
     * creating the equivalent ng-{@link ContentType}s focusing on keeping
     * all the associations in tact.
     */
    @Override
    public void convertAll() {
        ExportLogger.fetching("content types");
        List<com.arsdigita.cms.ContentType> trunkContentTypes = com.arsdigita
                .cms.ContentType.getAllObjects();

        ExportLogger.converting("content types");
        createContentTypesAndSetAssociations(trunkContentTypes);

        ExportLogger.newLine();
    }

    /**
     * Creates the equivalent ng-class of the {@code ContentType} and restores
     * the associations to other classes.
     *
     * @param trunkContentTypes List of all
     *                          {@link com.arsdigita.cms.ContentType}s from
     *                          this old trunk-system.
     */
    private void createContentTypesAndSetAssociations(final List<com
            .arsdigita.cms.ContentType> trunkContentTypes) {
        int processed = 0;

        for (com.arsdigita.cms.ContentType trunkContentType :
                trunkContentTypes) {

            // create content type
            ContentType contentType = new ContentType(trunkContentType);

            // set default lifecycle
            // -> will be done in ContentSectionConversion

            // set default workflow
            // -> will be done in ContentSectionConversion

            processed++;
        }
        ExportLogger.created("content types", processed);
    }

    public static ContentTypeConversion getInstance() {
        return instance;
    }
}
