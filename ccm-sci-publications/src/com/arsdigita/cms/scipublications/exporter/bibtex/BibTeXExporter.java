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
package com.arsdigita.cms.scipublications.exporter.bibtex;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.imexporter.PublicationFormat;
import com.arsdigita.cms.scipublications.exporter.SciPublicationsExporter;
import com.arsdigita.cms.scipublications.exporter.bibtex.converters.BibTeXConverter;
import com.arsdigita.cms.scipublications.exporter.bibtex.converters.BibTeXConverters;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import org.apache.log4j.Logger;

/**
 * An exporter for the BibTeX format. The actual conversion between the CCM
 * publication content types and the BibTeX format is done by the converters
 * in the <code>com.arsdigita.cms.scipublications.exporter.bibtex</code> which
 * are implementation the {@link BibTeXConverter} interface.
 *
 * @author Jens Pelzetter
 */
public class BibTeXExporter implements SciPublicationsExporter {

    private final static Logger logger = Logger.getLogger(BibTeXExporter.class);

    public PublicationFormat getSupportedFormat() {
        try {
            return new PublicationFormat("BibTeX",
                                         new MimeType("text", "x-bibtex"),
                                         "bib");
        } catch (MimeTypeParseException ex) {
            logger.warn("Failed to create MimeType for PublicationFormat."
                        + "Using null mimetype instead. Cause: ", ex);
            return new PublicationFormat("BibTeX",
                                         null,
                                         "bib");

        }
    }

    public String exportPublication(final Publication publication) {       
        return BibTeXConverters.getInstance().convert(publication);     
    }
}
