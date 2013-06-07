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
package com.arsdigita.cms.scipublications.exporter.ris;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.exporter.SciPublicationsExporter;
import com.arsdigita.cms.scipublications.imexporter.PublicationFormat;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import org.apache.log4j.Logger;

/**
 * Implementation of {@link SciPublicationsExporter} which exports publications
 * in the RIS format. The RIS format is described at 
 * <a href="http://www.refman.com/support/risformat_intro.asp">http://www.refman.com/support/risformat_intro.asp</a>
 * and 
 * <a href="http://www.adeptscience.co.uk/kb/article/FE26">http://www.adeptscience.co.uk/kb/article/FE26</a>.
 * The <code>RisExporter</code> uses implementations of the {@link RisConverter} 
 * interface provided by the {@link RisConverters} class to convert the
 * publication content items from CCM to RIS references.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class RisExporter implements SciPublicationsExporter {

    private static final Logger logger = Logger.getLogger(RisExporter.class);

    public PublicationFormat getSupportedFormat() {
        try {
            return new PublicationFormat("RIS",
                    new MimeType("text", "x-ris"),
                    "ris");
        } catch(MimeTypeParseException ex) {
             logger.warn("Failed to create MimeType for PublicationFormat."
                        + "Using null mimetype instead. Cause: ", ex);
            return new PublicationFormat("RIS",
                                         null,
                                         "ris");
        }
    }

    public String exportPublication(final Publication publication) {
        return RisConverters.getInstance().convert(publication);
    }
}
