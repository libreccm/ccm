/*
 * Copyright (c) 2014 Jens Pelzetter
 *
 * This library is free software, you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY, without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library, if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.scipublications.exporter.csv;

import com.arsdigita.cms.contenttypes.AuthorshipCollection;
import com.arsdigita.cms.contenttypes.Publication;
import static com.arsdigita.cms.scipublications.exporter.csv.CsvExporterConstants.*;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class PublicationConverter implements CsvConverter {

    @Override
    public Map<CsvExporterConstants, String> convert(final Publication publication) {

        if (publication == null) {
            throw new IllegalArgumentException("Can't convert null values");
        }

        final Map<CsvExporterConstants, String> values = new EnumMap<CsvExporterConstants, String>(
            CsvExporterConstants.class);

        values.put(PUBLICATION_ID, publication.getID().toString());
        values.put(PUBLICATION_TYPE, publication.getClass().getName());
        values.put(TITLE, publication.getTitle());
        values.put(YEAR, convertIntegerValue(publication.getYearOfPublication()));
        values.put(YEAR_FIRST_PUBLISHED, convertIntegerValue(publication.getYearFirstPublished()));
        values.put(LANGUAGE_OF_PUBLICATION, publication.getLanguageOfPublication());
        values.put(REVIEWED, convertBooleanValue(publication.getReviewed()));

        values.put(AUTHORS, convertAuthors(publication.getAuthors()));

        return values;
    }

    protected String convertAuthors(final AuthorshipCollection authors) {
        final StringBuilder authorsCol = new StringBuilder();

        while (authors.next()) {
            authorsCol.append(authors.getSurname());
            if (authors.getGivenName() != null) {
                authorsCol.append(", ");
                authorsCol.append(authors.getGivenName());
            }
            authorsCol.append(';');
        }

        return authorsCol.toString();
    }

    protected String convertIntegerValue(final Integer value) {
        if (value == null) {
            return "";
        } else {
            return value.toString();
        }
    }

    protected String convertBooleanValue(final Boolean value) {
        if (value == null) {
            return "";
        } else {
            return value.toString();
        }
    }

    protected String convertDateValue(final Date value) {
        if (value == null) {
            return "";
        } else {
            final Calendar calender = Calendar.getInstance();
            calender.setTime(value);

            return String.format("%d-%d-%d", calender.get(Calendar.YEAR),
                                 calender.get(Calendar.MONTH),
                                 calender.get(Calendar.DAY_OF_MONTH));

        }
    }

    @Override
    public String getCCMType() {
        return Publication.class.getName();
    }

}
