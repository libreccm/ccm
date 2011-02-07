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
 */package com.arsdigita.cms.scipublications.exporter.bibtex.converters;

import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.BibTeXBuilder;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.BibTeXField;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.UnsupportedFieldException;
import org.apache.log4j.Logger;

/**
 * Converts an {@link InProceedings} item to an BibTeX 
 * <code>inproceedings</code> reference.
 *
 * @author Jens Pelzetter
 */
public class InProceedingsConverter extends AbstractBibTeXConverter {

    private static final Logger logger = Logger.getLogger(
            InProceedingsConverter.class);

    @Override
    protected String getBibTeXType() {
        return "inproceedings";
    }

    public String convert(final Publication publication) {
        BibTeXBuilder builder;
        InProceedings inProceedings;

        if (!(publication instanceof InProceedings)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The InProceedingsConverter only "
                                  + "supports publication types which are of the"
                                  + "type InProceedings or which are "
                                  + "extending "
                                  + "InProceedings. The "
                                  + "provided publication is of type '%s' which "
                                  + "is not of type "
                                  + "InProceedings and does not "
                                  + "extends InProceedings.",
                                  publication.getClass().getName()));
        }

        inProceedings = (InProceedings) publication;

        convertAuthors(publication);
        builder = getBibTeXBuilder();
        try {
            convertTitle(publication);
            convertYear(publication);

            if (inProceedings.getPagesFrom() != null) {
                builder.setField(BibTeXField.PAGES,
                                 String.format("%s - %s",
                                               inProceedings.getPagesFrom(),
                                               inProceedings.getPagesTo()));
            }

            if (inProceedings.getProceedings() == null) {
                builder.setField(BibTeXField.BOOKTITLE, "");
            } else {
                Proceedings proceedings;

                proceedings = inProceedings.getProceedings();

                builder.setField(BibTeXField.BOOKTITLE,
                                 proceedings.getTitle());

                convertVolume(proceedings);
                convertSeries(proceedings);

                if (proceedings.getOrganizerOfConference() != null) {
                    builder.setField(BibTeXField.ORGANIZATION,
                            proceedings.getOrganizerOfConference().getTitle());
                }

                convertPublisher(proceedings);
            }            
        } catch (UnsupportedFieldException ex) {
            logger.warn("Tried to set unsupported BibTeX field while "
                        + "converting a publication");
        }

        return builder.toBibTeX();
    }

    public String getCcmType() {
        return InProceedings.class.getName();
    }
}
