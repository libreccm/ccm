/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
package com.arsdigita.cms.scipublications.exporter.ris;

import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.contenttypes.AuthorshipCollection;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import com.arsdigita.cms.contenttypes.SeriesCollection;

/**
 * An abstract base implementation of the {@link RisConverter} interface providing common functionality.
 *
 * @author Jens Pelzetter
 */
public abstract class AbstractRisConverter implements RisConverter {

    private final RisBuilder risBuilder = new RisBuilder();

    protected void convertAuthors(final Publication publication) {
        final AuthorshipCollection authors = publication.getAuthors();

        if (authors != null) {
            while (authors.next()) {
                if (authors.isEditor()) {
                    getRisBuilder().addField(RisField.ED, String.format("%s,%s",
                                                                         authors.getAuthor().getSurname(),
                                                                         authors.getAuthor().getGivenName()));
                } else {
                    getRisBuilder().addField(RisField.AU, String.format("%s,%s",
                                                                         authors.getAuthor().getSurname(),
                                                                         authors.getAuthor().getGivenName()));
                }
            }
        }
    }

    protected void convertTitle(final Publication publication) {
        getRisBuilder().addField(RisField.TI, publication.getTitle());
    }

    protected void convertYear(final Publication publication) {
        getRisBuilder().addField(RisField.PY, String.format("%d///", publication.getYearOfPublication()));
    }

    protected void convertPublisher(final PublicationWithPublisher publication) {
        convertPublisher(publication, RisField.CY);
    }

    protected void convertPublisher(final PublicationWithPublisher publication, final RisField placeField) {
        if (publication.getPublisher() != null) {
            if ((publication.getPublisher().getPlace() != null) && !(publication.getPublisher().getPlace().isEmpty())) {
                getRisBuilder().addField(placeField, publication.getPublisher().getPlace());
            }

            getRisBuilder().addField(RisField.PB, publication.getPublisher().getTitle());
        }
    }

    protected void convertISBN(final PublicationWithPublisher publication) {
        if (publication.getISBN() != null) {
            getRisBuilder().addField(RisField.SN, publication.getISBN());
        }
    }

    protected void convertVolume(final PublicationWithPublisher publication) {
        if (publication.getVolume() != null) {
            getRisBuilder().addField(RisField.VL, publication.getVolume().toString());
        }
    }

    protected void convertEdition(final PublicationWithPublisher publication) {
        if (publication.getEdition() != null) {
            getRisBuilder().addField(RisField.ET, publication.getEdition());
        }
    }

    protected void convertSeries(final Publication publication) {
        SeriesCollection seriesColl = publication.getSeries();
        if ((seriesColl != null) && (seriesColl.size() > 0)) {

            seriesColl.next();

            getRisBuilder().addField(RisField.T3, seriesColl.getSeries().getTitle());

            seriesColl.close();
        }
    }

    protected RisBuilder getRisBuilder() {
        return risBuilder;
    }

}
