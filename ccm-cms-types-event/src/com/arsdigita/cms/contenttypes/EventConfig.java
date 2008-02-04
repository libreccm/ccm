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

public class EventConfig extends AbstractConfig {
    
    private final Parameter m_hideDateDescription;
    private final Parameter m_hideMainContributor;
    private final Parameter m_hideEventType;
    private final Parameter m_hideLinkToMap;
    private final Parameter m_hideCost;
    private final Parameter m_useHtmlDateDescription;

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
	
        register(m_hideDateDescription);
	register(m_hideMainContributor);
	register(m_hideEventType);
	register(m_hideLinkToMap);
	register(m_hideCost);
        register(m_useHtmlDateDescription);

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
}
 
