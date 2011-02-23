/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.IntegerParameter;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ImageConfig extends AbstractConfig {

    private final Parameter m_startYear;
    private final Parameter m_endYearDelta;
    private final Parameter m_maxImageWidth;
    private final Parameter m_maxThumbnailWidth;

    public ImageConfig() {
        m_startYear = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.image.start_year",
                Parameter.REQUIRED,
                new Integer(GregorianCalendar.getInstance().get(Calendar.YEAR) - 1));

        m_endYearDelta = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.image.end_year_delta",
                Parameter.REQUIRED,
                new Integer(3));

        m_maxImageWidth = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.image.max_image_width",
                Parameter.REQUIRED,
                new Integer(600));

        m_maxThumbnailWidth = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.image.max_thumbnail_width",
                Parameter.REQUIRED,
                new Integer(150));

        register(m_startYear);
        register(m_endYearDelta);
        register(m_maxImageWidth);
        register(m_maxThumbnailWidth);

        loadInfo();
    }

    public final int getStartYear() {
        return ((Integer) get(m_startYear)).intValue();
    }

    public final int getEndYearDelta() {
        return ((Integer) get(m_endYearDelta)).intValue();
    }

    public final int getMaxImageWidth() {
        return ((Integer) get(m_maxImageWidth)).intValue();
    }

    public final int getMaxThumbnailWidth() {
        return ((Integer) get(m_maxThumbnailWidth)).intValue();
    }
}
