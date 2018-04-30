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

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.portation.conversion.NgCmsArticleCollection;
import com.arsdigita.cms.portation.modules.contentsection.ContentItem;
import com.arsdigita.portation.Portable;
import com.arsdigita.portation.modules.core.l10n.LocalizedString;

import java.util.Locale;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 4/3/18
 */
public class Article extends ContentItem implements Portable {
    private LocalizedString text;

    /**
     * Constructor for the ng-object.
     *
     * @param trunkArticle the trunk object
     */
    public Article(final com.arsdigita.cms.contenttypes.Article trunkArticle) {
        super(trunkArticle);

        this.text = new LocalizedString();

        final ContentBundle languageBundle = trunkArticle.getContentBundle();
        if (languageBundle != null) {
            final ItemCollection languageSets = languageBundle.getInstances();
            while (languageSets.next()) {
                final Locale language = new Locale(languageSets.getLanguage());
                final com.arsdigita.cms.contenttypes.Article languageItem =
                        (com.arsdigita.cms.contenttypes.Article) languageSets
                                .getContentItem();

                addName(language, languageItem.getName());
                addTitle(language, languageItem.getTitle());
                addDescription(language, languageItem.getDescription());

                final String text = languageItem.getTextAsset() != null
                        ? languageItem.getTextAsset().getText()
                        : "";
                this.text.addValue(language, text);
            }
            languageSets.close();
        }

        NgCmsArticleCollection.articles.put(this.getObjectId(), this);
    }

    public LocalizedString getText() {
        return text;
    }

    public void setText(final LocalizedString text) {
        this.text = text;
    }
}
