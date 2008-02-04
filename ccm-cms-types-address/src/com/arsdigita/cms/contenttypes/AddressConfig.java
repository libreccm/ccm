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

public class AddressConfig extends AbstractConfig {
    
    private final Parameter m_hideCountryCodeSelection;
    private final Parameter m_hidePostalCode;

    public AddressConfig() {
        m_hideCountryCodeSelection = new BooleanParameter(
			"com.arsdigita.cms.contenttypes.address.hide_country_code_selection",
			Parameter.REQUIRED,
			new Boolean(false));
	
	m_hidePostalCode = new BooleanParameter(
			"com.arsdigita.cms.contenttypes.address.hide_postal_code",
			Parameter.REQUIRED,
			new Boolean(false));
	
	
        register(m_hideCountryCodeSelection);
	register(m_hidePostalCode);

        loadInfo();
    }
    
    public final boolean getHideCountryCodeSelection() {
	    return ((Boolean) get(m_hideCountryCodeSelection)).booleanValue();
    }
    public final boolean getHidePostalCode() {
	    return ((Boolean) get(m_hidePostalCode)).booleanValue();
    }
}
 
