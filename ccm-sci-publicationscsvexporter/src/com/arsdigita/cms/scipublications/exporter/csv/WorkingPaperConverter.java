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

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.WorkingPaper;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class WorkingPaperConverter extends UnPublishedConverter {

    @Override
    public Map<CsvExporterConstants, String> convert(final Publication publication) {

        if (!(publication instanceof WorkingPaper)) {
            throw new UnsupportedCcmTypeException(
                String.format("The WorkingPaperConverter only "
                              + "supports publication types which are of the"
                              + "type WorkingPaper or which are extending "
                              + "WorkingPaper. The "
                              + "provided publication is of type '%s' which "
                              + "is not of type WorkingPaper and does not "
                              + "extends PubliccationWithPublisher.",
                              publication.getClass().getName()));
        }

        return super.convert(publication);

    }

    @Override
    public String getCCMType() {
        return WorkingPaper.class.getName();
    }

}
