/*
 * Copyright (c) 2010 Jens Pelzetter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.scipublications.exporter.bibtex.converters;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.SeriesCollection;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.BibTeXBuilder;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.BibTeXField;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.UnsupportedFieldException;
import org.apache.log4j.Logger;

/**
 * Converts an {@link ArticleInCollectedVolume} to a BibTeX
 * <code>incollection</code> reference.
 *
 * @author Jens Pelzetter
 * @version $Id: ArticleInCollectedVolumeConverter.java 740 2011-02-07 18:56:18Z
 * jensp $
 */
public class ArticleInCollectedVolumeConverter extends AbstractBibTeXConverter {

    private static final Logger logger = Logger.getLogger(
            ArticleInCollectedVolumeConverter.class);

    @Override
    protected String getBibTeXType() {
        return "incollection";
    }

    public String convert(final Publication publication) {
        ArticleInCollectedVolume article;
        BibTeXBuilder builder;

        if (!(publication instanceof ArticleInCollectedVolume)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The ArticleInCollectedVolumeConverter only "
                                  + "supports publication types which are of the"
                                  + "type ArticleInCollectedVolume or which are "
                                  + "extending "
                                  + "ArticleInCollectedVolume. The "
                                  + "provided publication is of type '%s' which "
                                  + "is not of type "
                                  + "ArticleInCollectedVolume and does not "
                                  + "extends ArticleInCollectedVolume.",
                                  publication.getClass().getName()));
        }

        article = (ArticleInCollectedVolume) publication;

        convertAuthors(publication);
        builder = getBibTeXBuilder();
        try {
            convertTitle(publication);
            convertYear(publication);

            if (article.getCollectedVolume() != null) {
                builder.setField(BibTeXField.BOOKTITLE,
                                 article.getCollectedVolume().getTitle());

                if (article.getCollectedVolume().getPublisher() == null) {
                    builder.setField(BibTeXField.PUBLISHER, "");
                } else {
                    builder.setField(BibTeXField.PUBLISHER,
                                     article.getCollectedVolume().getPublisher().
                            getTitle());
                }

                if (article.getCollectedVolume().getVolume() != null) {
                    builder.setField(BibTeXField.VOLUME,
                                     article.getCollectedVolume().getVolume().
                            toString());
                }

                if (article.getCollectedVolume().getEdition() != null) {
                    builder.setField(BibTeXField.EDITION,
                                     article.getCollectedVolume().getEdition());
                }

                SeriesCollection seriesColl =
                                 article.getCollectedVolume().getSeries();
                if ((seriesColl != null) && (seriesColl.size() > 0)) {

                    seriesColl.next();

                    builder.setField(BibTeXField.SERIES,
                                     seriesColl.getSeries().getTitle());

                    seriesColl.close();
                }
            }

            if (article.getChapter() != null) {
                builder.setField(BibTeXField.CHAPTER, article.getChapter());
            }

            if (article.getPagesFrom() != null) {
                if (article.getPagesTo() == null) {
                    builder.setField(BibTeXField.PAGES,
                                     String.format("%s",
                                                   article.getPagesFrom()));
                } else {
                    builder.setField(BibTeXField.PAGES,
                                     String.format("%s - %s",
                                                   article.getPagesFrom(),
                                                   article.getPagesTo()));
                }
            }


        } catch (UnsupportedFieldException ex) {
            logger.warn("Tried to set unsupported BibTeX field while "
                        + "converting a publication", ex);
        }

        return builder.toBibTeX();
    }

    public String getCcmType() {
        return ArticleInCollectedVolume.class.getName();
    }
}
