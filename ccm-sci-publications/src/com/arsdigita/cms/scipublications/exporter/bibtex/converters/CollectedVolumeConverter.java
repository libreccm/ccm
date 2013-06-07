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
import com.arsdigita.cms.contenttypes.CollectedVolume;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.UnsupportedFieldException;
import org.apache.log4j.Logger;

/**
 * Converts a {@link CollectedVolume} to a BibTeX <code>book</code> reference.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class CollectedVolumeConverter extends AbstractBibTeXConverter {

    private static final Logger logger = Logger.getLogger(
            CollectedVolumeConverter.class);

    @Override
    protected String getBibTeXType() {
        return "book";
    }

    public String convert(final Publication publication) {
        CollectedVolume collectedVolume;

        if (!(publication instanceof CollectedVolume)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The PublicationWithPublicationConverter only "
                                  + "supports publication types which are "
                                  + "extending CollectedVolume. The "
                                  + "provided publication is of type '%s' which "
                                  + "does not extends "
                                  + "CollectedVolume.",
                                  publication.getClass().getName()));
        }

        collectedVolume = (CollectedVolume) publication;

        convertAuthors(publication);
        try {
            convertTitle(publication);
            convertYear(publication);

            convertPublisher(collectedVolume);
            convertISBN(collectedVolume);
            convertEdition(collectedVolume);
        } catch (UnsupportedFieldException ex) {
            logger.warn("Tried to set unsupported BibTeX field while "
                        + "converting a publication");
        }

        return getBibTeXBuilder().toBibTeX();
    }

    public String getCcmType() {
        return CollectedVolume.class.getName();
    }
}
