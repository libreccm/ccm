/*
 * Copyright (C) 2005 Runtime Collective Ltd. All Rights Reserved.
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
package com.arsdigita.xml;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.util.Assert;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.EnumerationParameter;
import com.arsdigita.util.parameter.ErrorList;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ParameterError;

import org.apache.log4j.Logger;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**
 * Stores the configuration record for the XML functionality
 */
public final class XMLConfig extends AbstractConfig {

    private static Logger s_log = Logger.getLogger(XMLConfig.class);

    private Parameter m_activateFullTimeFormatter;

    public XMLConfig() {

        m_activateFullTimeFormatter = new BooleanParameter
            ("waf.xml.activate_full_date_formatter", 
             Parameter.OPTIONAL, 
             new Boolean(false));

        register(m_activateFullTimeFormatter);

        loadInfo();
    }

    /**
     * Returns the activateFullTimeFormatter flag.
     */
    public boolean getActivateFullTimeFormatter() {
        return ((Boolean) get(m_activateFullTimeFormatter)).booleanValue();
    }

    /**
     * Sets the activateFullTimeFormatter flag.
     */
    public void setActivateFullTimeFormatter(boolean activateFullTimeFormatter) {
        set (m_activateFullTimeFormatter,new Boolean(activateFullTimeFormatter));
    }
}
