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
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class NewsItemConfig extends AbstractConfig {
    
    private final Parameter m_hideHomepageField;
    private final Parameter m_startYear;
    private final Parameter m_endYearDelta;

    public NewsItemConfig() {
        m_hideHomepageField = new BooleanParameter(
            "com.arsdigita.cms.contenttypes.newsitem.hide_homepage",
            Parameter.REQUIRED,
            new Boolean(false));
        m_startYear = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.newsitem.start_year",
                Parameter.REQUIRED,
                new Integer(GregorianCalendar.getInstance().get(Calendar.YEAR) - 1));
        
        m_endYearDelta = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.newsitem.end_year_delta",
                Parameter.REQUIRED,
                new Integer(3));
        
        
        register(m_hideHomepageField);
        register(m_startYear);
        register(m_endYearDelta);

        loadInfo();
    }
    
    public final boolean getHideHomepageField() {
        return ((Boolean) get(m_hideHomepageField)).booleanValue();
    }
    public final int getStartYear() {
        return ((Integer) get(m_startYear)).intValue();
    }
    public final int getEndYearDelta() {
        return ((Integer) get(m_endYearDelta)).intValue();
    }

}
