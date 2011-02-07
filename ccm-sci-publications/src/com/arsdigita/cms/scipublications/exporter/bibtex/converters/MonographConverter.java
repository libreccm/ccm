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
package com.arsdigita.cms.scipublications.exporter.bibtex.converters;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.Monograph;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.UnsupportedFieldException;
import org.apache.log4j.Logger;

/**
 * Converts a {@link Monograph} to a BibTeX <code>book</code> reference.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class MonographConverter extends AbstractBibTeXConverter {

    private static final Logger logger = Logger.getLogger(
            MonographConverter.class);

    @Override
    protected String getBibTeXType() {
        return "book";
    }

    public String convert(final Publication publication) {
        Monograph monograph;

        if (!(publication instanceof Monograph)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The MonographConverter only "
                                  + "supports publication types which are of the"
                                  + "type Monograph or which are extending "
                                  + "Monograh. The "
                                  + "provided publication is of type '%s' which "
                                  + "is not of type Monograph and does not "
                                  + "extends Monograph.",
                                  publication.getClass().getName()));
        }

        monograph = (Monograph) publication;

        convertAuthors(publication);
        try {
            convertTitle(publication);
            convertYear(publication);

            convertPublisher(monograph);
            convertISBN(monograph);
            convertEdition(monograph);
        } catch (UnsupportedFieldException ex) {
            logger.warn("Tried to set unsupported BibTeX field while "
                        + "converting a publication", ex);
        }

        return getBibTeXBuilder().toBibTeX();
    }

    public String getCcmType() {
        return Monograph.class.getName();
    }
}
