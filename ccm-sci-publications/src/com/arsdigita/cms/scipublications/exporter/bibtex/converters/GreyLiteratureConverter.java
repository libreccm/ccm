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

import com.arsdigita.cms.contenttypes.GreyLiterature;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.BibTeXBuilder;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.UnsupportedFieldException;
import org.apache.log4j.Logger;

/**
 * Converts a {@link GreyLiterature} item to a BibTeX <code>misc</code>
 * reference.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class GreyLiteratureConverter extends AbstractBibTeXConverter {

    private static final Logger logger = Logger.getLogger(
            GreyLiteratureConverter.class);

    @Override
    protected String getBibTeXType() {
        return "misc";
    }

    public String convert(Publication publication) {
        BibTeXBuilder builder;
        GreyLiterature greyLiterature;

        if (!(publication instanceof GreyLiterature)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The GreyLiteratureConverter only "
                                  + "supports publication types which are of the"
                                  + "type GreyLiterature or which are "
                                  + "extending "
                                  + "GreyLiterature. The "
                                  + "provided publication is of type '%s' which "
                                  + "is not of type "
                                  + "GreyLiterature and does not "
                                  + "extends GreyLiterature.",
                                  publication.getClass().getName()));
        }

        greyLiterature = (GreyLiterature) publication;

        convertAuthors(publication);
        builder = getBibTeXBuilder();
        try {
            convertTitle(publication);
            convertYear(publication);
        } catch (UnsupportedFieldException ex) {
            logger.warn("Tried to set unsupported BibTeX field while "
                        + "converting a publication");
        }

        return builder.toBibTeX();
    }

    public String getCcmType() {
        return GreyLiterature.class.getName();
    }
}
