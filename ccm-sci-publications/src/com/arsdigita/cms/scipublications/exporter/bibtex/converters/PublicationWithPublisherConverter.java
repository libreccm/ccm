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
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.UnsupportedFieldException;
import org.apache.log4j.Logger;

/**
 * Converts a {@link PublicationWithPublisher} to a BibTeX <code>misc</code>
 * reference. This converter is used as a fallback by the
 * {@link BibTeXConverters} class if no suitable converter can be found and
 * the publication to convert is a {@link PublicationWithPublisher}.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PublicationWithPublisherConverter extends AbstractBibTeXConverter {

    private static final Logger logger = Logger.getLogger(
            PublicationWithPublisherConverter.class);

    @Override
    protected String getBibTeXType() {
        return "misc";
    }

    public String convert(final Publication publication) {
        PublicationWithPublisher _publication;

        if (!(publication instanceof PublicationWithPublisher)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The PublicationWithPublicationConverter only "
                                  + "supports publication types which are "
                                  + "extending PublicationWithPublisher. The "
                                  + "provided publication is of type '%s' which "
                                  + "does not extends "
                                  + "PublicationWithPublisher.",
                                  publication.getClass().getName()));
        }

        _publication = (PublicationWithPublisher) publication;

        convertAuthors(publication);
        try {
            convertTitle(publication);
            convertYear(publication);

            convertPublisher(_publication);
            convertISBN(_publication);
            convertEdition(_publication);
        } catch (UnsupportedFieldException ex) {
            logger.warn("Tried to set unsupported BibTeX field while "
                        + "converting a publication");
        }

        return getBibTeXBuilder().toBibTeX();
    }

    public String getCcmType() {
        return PublicationWithPublisher.class.getName();
    }
}
