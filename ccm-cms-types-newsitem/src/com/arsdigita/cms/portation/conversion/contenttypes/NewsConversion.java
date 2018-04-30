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

import com.arsdigita.cms.contenttypes.NewsItem;
import com.arsdigita.cms.portation.modules.contenttypes.News;
import com.arsdigita.portation.AbstractConversion;
import com.arsdigita.portation.cmd.ExportLogger;

import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 4/20/18
 */
public class NewsConversion extends AbstractConversion {
    /**
     * Retrieves all trunk-{@link com.arsdigita.cms.contenttypes.NewsItem}s
     * from the persistent storage and collects them in a list. Then calls for
     * creating the equivalent ng-{@link News}s focusing on keeping all the
     * associations in tact.
     */
    @Override
    public void convertAll() {
        ExportLogger.fetching("news");
        List<NewsItem> allTrunkNews = NewsItem.getAllObjects();

        ExportLogger.converting("news");
        createNewsAndSetAssociations(allTrunkNews);

        ExportLogger.newLine();
    }

    /**
     * Creates the equivalent ng-class of the {@code News} and restores the
     * associations to other classes.
     *
     * @param allTrunkNews List of all
     *                  {@link com.arsdigita.cms.contenttypes.NewsItem}s
     *                  from this old trunk-system.
     */
    private void createNewsAndSetAssociations(final List<NewsItem> allTrunkNews) {
        int processed = 0;
        for (NewsItem trunkNews : allTrunkNews) {

            // create news
            News news = new News(trunkNews);

            processed++;
        }
        ExportLogger.created("news", processed);
    }
}
