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

public class EventConfig extends AbstractConfig {
    
    private final Parameter m_hideDateDescription;
    private final Parameter m_hideMainContributor;
    private final Parameter m_hideEventType;
    private final Parameter m_hideLinkToMap;
    private final Parameter m_hideCost;
    private final Parameter m_useHtmlDateDescription;
    private final Parameter m_startYear;
    private final Parameter m_endYearDelta;
    private final Parameter m_leadTextOptional;
    private final Parameter m_startTimeOptional;
    
    public EventConfig() {
        m_hideDateDescription = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.event.hide_date_description",
                Parameter.REQUIRED,
                new Boolean(false));
        
        m_hideMainContributor = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.event.hide_main_contributor",
                Parameter.REQUIRED,
                new Boolean(false));
        
        m_hideEventType = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.event.hide_event_type",
                Parameter.REQUIRED,
                new Boolean(false));
        
        m_hideLinkToMap = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.event.hide_link_to_map",
                Parameter.REQUIRED,
                new Boolean(false));
        
        m_hideCost = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.event.hide_cost",
                Parameter.REQUIRED,
                new Boolean(false));
        
        m_useHtmlDateDescription = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.event.use_html_date_description",
                Parameter.REQUIRED,
                new Boolean(true));   // depricated, may be false in future releases
        
        m_startYear = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.event.start_year",
                Parameter.REQUIRED,
                new Integer(GregorianCalendar.getInstance().get(Calendar.YEAR) - 1));
        
        m_endYearDelta = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.event.end_year_delta",
                Parameter.REQUIRED,
                new Integer(3));
        
        m_leadTextOptional = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.event.lead_text_optional",
                Parameter.REQUIRED,
                new Boolean(false));
        
        m_startTimeOptional = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.event.start_time_optional",
                Parameter.REQUIRED,
                new Boolean(false));
        
        register(m_hideDateDescription);
        register(m_hideMainContributor);
        register(m_hideEventType);
        register(m_hideLinkToMap);
        register(m_hideCost);
        register(m_useHtmlDateDescription);
        register(m_startYear);
        register(m_endYearDelta);
        register(m_leadTextOptional);
        register(m_startTimeOptional);
        
        loadInfo();
    }
    
    public final boolean getHideDateDescription() {
        return ((Boolean) get(m_hideDateDescription)).booleanValue();
    }
    public final boolean getHideMainContributor() {
        return ((Boolean) get(m_hideMainContributor)).booleanValue();
    }
    public final boolean getHideEventType() {
        return ((Boolean) get(m_hideEventType)).booleanValue();
    }
    public final boolean getHideLinkToMap() {
        return ((Boolean) get(m_hideLinkToMap)).booleanValue();
    }
    public final boolean getHideCost() {
        return ((Boolean) get(m_hideCost)).booleanValue();
    }
    public final boolean getUseHtmlDateDescription() {
        return ((Boolean) get(m_useHtmlDateDescription)).booleanValue();
    }
    public final int getStartYear() {
        return ((Integer) get(m_startYear)).intValue();
    }
    public final int getEndYearDelta() {
        return ((Integer) get(m_endYearDelta)).intValue();
    }
    public final boolean isLeadTextOptional() {
        return ((Boolean) get(m_leadTextOptional)).booleanValue();
    }
    public final boolean isStartTimeOptional() {
        return ((Boolean) get(m_startTimeOptional)).booleanValue();
    }
}

