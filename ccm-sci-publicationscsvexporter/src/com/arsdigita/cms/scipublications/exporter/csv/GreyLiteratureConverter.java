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

import com.arsdigita.cms.contenttypes.GreyLiterature;
import com.arsdigita.cms.contenttypes.Publication;
import static com.arsdigita.cms.scipublications.exporter.csv.CsvExporterConstants.*;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class GreyLiteratureConverter extends UnPublishedConverter {

    @Override
    public Map<CsvExporterConstants, String> convert(final Publication publication) {

        if (!(publication instanceof GreyLiterature)) {
            throw new UnsupportedCcmTypeException(
                String.format("The GreyLiteratureConverter only "
                              + "supports publication types which are of the"
                              + "type GreyLiterature or which are extending "
                              + "GreyLiterature. The "
                              + "provided publication is of type '%s' which "
                              + "is not of type GreyLiterature and does not "
                              + "extends PubliccationWithPublisher.",
                              publication.getClass().getName()));
        }

        final GreyLiterature greyLiterature = (GreyLiterature) publication;

        final Map<CsvExporterConstants, String> values = super.convert(publication);

        values.put(PAGES_FROM, convertIntegerValue(greyLiterature.getPagesFrom()));
        values.put(PAGES_TO, convertIntegerValue(greyLiterature.getPagesTo()));

        return values;
    }

    @Override
    public String getCCMType() {
        return GreyLiterature.class.getName();
    }

}
