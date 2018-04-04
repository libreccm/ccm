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
package com.arsdigita.cms.portation.modules.contenttypes;

import com.arsdigita.cms.portation.conversion.NgCmsCollection;
import com.arsdigita.cms.portation.modules.contentsection.ContentItem;
import com.arsdigita.portation.Portable;
import com.arsdigita.portation.modules.core.l10n.LocalizedString;

import java.util.Date;
import java.util.Locale;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 4/3/18
 */
public class News extends ContentItem implements Portable {
    private LocalizedString text;
    private Date releaseDate;
    private boolean homepage;

    /**
     * Constructor for the ng-object.
     *
     * @param trunkNews the trunk object
     */
    public News(final com.arsdigita.cms.contenttypes.NewsItem trunkNews) {
        super(trunkNews);

        final Locale locale = Locale.getDefault();
        this.text = new LocalizedString();
        this.text.addValue(locale, trunkNews.getTextAsset().getText());

        this.releaseDate = trunkNews.getLaunchDate();

        this.homepage = trunkNews.isHomepage();

        NgCmsCollection.news.put(this.getObjectId(), this);
    }

    public LocalizedString getText() {
        return text;
    }

    public void setText(final LocalizedString text) {
        this.text = text;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(final Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isHomepage() {
        return homepage;
    }

    public void setHomepage(final boolean homepage) {
        this.homepage = homepage;
    }
}
