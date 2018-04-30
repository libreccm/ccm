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
package com.arsdigita.cms.portation.conversion.contenttypes;

import com.arsdigita.cms.contenttypes.ArticleSection;
import com.arsdigita.cms.portation.modules.contenttypes.MultiPartArticleSection;
import com.arsdigita.portation.AbstractConversion;
import com.arsdigita.portation.cmd.ExportLogger;

import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 4/20/18
 */
public class MultiPartArticleSectionConversion extends AbstractConversion {
    /**
     * Retrieves all
     * trunk-{@link ArticleSection}s from the
     * persistent storage and collects them in a list. Then calls for
     * creating the equivalent ng-{@link MultiPartArticleSection}s focusing on
     * keeping all the associations in tact.
     */
    @Override
    public void convertAll() {
        ExportLogger.fetching("mp article sections");
        List<ArticleSection> trunkMPArticleSections = ArticleSection
                .getAllObjects();

        ExportLogger.converting("mp article sections");
        createMPArticleSectionsAndSetAssociations(trunkMPArticleSections);

        ExportLogger.newLine();
    }

    /**
     * Creates the equivalent ng-class of the {@code MultiPartArticleSection}
     * and restores the associations to other classes.
     *
     * @param trunkMPArticleSections List of all
     *                    {@link com.arsdigita.cms.contenttypes.ArticleSection}s
     *                    from this old trunk-system.
     */
    private void createMPArticleSectionsAndSetAssociations(final List<
            ArticleSection> trunkMPArticleSections) {
        int processed = 0;
        for (ArticleSection trunkMPArticleSection : trunkMPArticleSections) {

            // create mp article section
            MultiPartArticleSection multiPartArticleSection = new
                    MultiPartArticleSection(trunkMPArticleSection);

            processed++;
        }
        ExportLogger.created("mp article sections", processed);
    }
}
