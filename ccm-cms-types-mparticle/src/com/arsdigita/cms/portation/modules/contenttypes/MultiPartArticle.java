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
import com.fasterxml.jackson.annotation.JsonIdentityReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 4/3/18
 */
public class MultiPartArticle extends ContentItem implements Portable {
    private LocalizedString summary;
    @JsonIdentityReference(alwaysAsId = true)
    private List<MultiPartArticleSection> sections;

    /**
     * Constructor for the ng-object.
     *
     * @param trunkMultiPartArticle the trunk object
     */
    public MultiPartArticle(final com.arsdigita.cms.contenttypes
            .MultiPartArticle trunkMultiPartArticle) {
        super(trunkMultiPartArticle);

        final Locale locale = Locale.getDefault();
        this.summary = new LocalizedString();
        this.summary.addValue(locale, trunkMultiPartArticle.getSummary());

        this.sections = new ArrayList<>();

        NgCmsCollection.multiPartArticles.put(this.getObjectId(), this);
    }

    public LocalizedString getSummary() {
        return summary;
    }

    public void setSummary(final LocalizedString summary) {
        this.summary = summary;
    }

    public List<MultiPartArticleSection> getSections() {
        return sections;
    }

    public void setSections(final List<MultiPartArticleSection> sections) {
        this.sections = sections;
    }
}
