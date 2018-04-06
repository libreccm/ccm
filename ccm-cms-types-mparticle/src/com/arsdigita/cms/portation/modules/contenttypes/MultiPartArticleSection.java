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

import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.contenttypes.ArticleSection;
import com.arsdigita.cms.portation.conversion.NgCmsCollection;
import com.arsdigita.portation.Portable;
import com.arsdigita.portation.modules.core.l10n.LocalizedString;

import java.util.Locale;
import java.util.Objects;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 4/4/18
 */
public class MultiPartArticleSection implements Portable {
    private long sectionId;
    private LocalizedString title;
    private int rank;
    private boolean pageBreak;
    private LocalizedString text;

    public MultiPartArticleSection(final ArticleSection
                                           trunkMultiPartArticleSection) {
        this.sectionId = trunkMultiPartArticleSection.getID().longValue();

        this.title = new LocalizedString();
        this.rank = trunkMultiPartArticleSection.getRank();
        this.pageBreak = trunkMultiPartArticleSection.isPageBreak();
        this.text = new LocalizedString();

        final ItemCollection languageSets = Objects.requireNonNull(
                trunkMultiPartArticleSection.getContentBundle())
                                            .getInstances();
        while (languageSets.next()) {
            final Locale language = new Locale(languageSets.getLanguage());
            final ArticleSection languageItem = (ArticleSection) languageSets
                    .getContentItem();

            this.title.addValue(language, languageItem.getTitle());
            this.text.addValue(language, languageItem.getText().getText());
        }

        NgCmsCollection.multiPartArticleSections.put(this.sectionId, this);
    }

    public long getSectionId() {
        return sectionId;
    }

    public void setSectionId(final long sectionId) {
        this.sectionId = sectionId;
    }

    public LocalizedString getTitle() {
        return title;
    }

    public void setTitle(final LocalizedString title) {
        this.title = title;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }

    public boolean isPageBreak() {
        return pageBreak;
    }

    public void setPageBreak(final boolean pageBreak) {
        this.pageBreak = pageBreak;
    }

    public LocalizedString getText() {
        return text;
    }

    public void setText(final LocalizedString text) {
        this.text = text;
    }
}
