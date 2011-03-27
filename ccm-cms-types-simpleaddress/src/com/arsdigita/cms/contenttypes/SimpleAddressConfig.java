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

import org.apache.log4j.Logger;

public class SimpleAddressConfig extends AbstractConfig {

    /** A logger instance.  */
    private static final Logger s_log = Logger.getLogger(SimpleAddressConfig.class);

    /** Singelton config object.  */
    private static SimpleAddressConfig s_conf;

    /**
     * Gain a SimpleAddressConfig object.
     *
     * Singelton pattern, don't instantiate a config object using the
     * constructor directly!
     * @return
     */
    public static synchronized SimpleAddressConfig getConfig() {
        if (s_conf == null) {
            s_conf = new SimpleAddressConfig();
            s_conf.load();
        }

        return s_conf;
    }

    // set of configuration parameters
    /** Hide the country selection step in authoring form */
    private final Parameter m_hideCountryCodeSelection =
            new BooleanParameter(
			    "cms.contenttypes.simpleaddress.hide_country_code_selection",
                Parameter.REQUIRED,
                new Boolean(false));
    /** Hide the postal code entry step in authoring form */
    private final Parameter m_hidePostalCode =
	        new BooleanParameter(
			    "cms.contenttypes.simpleaddress.hide_postal_code",
			    Parameter.REQUIRED,
			    new Boolean(false));

    /** 
     * Constructor
     */
    public SimpleAddressConfig() {

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
 
