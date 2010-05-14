/*
 * Copyright (C) 2008 Sören Bernstein All Rights Reserved.
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
package com.arsdigita.categorization;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;

import org.apache.log4j.Logger;

/**
 * Stores the configuration record for the Categorization functionality.
 *
 * @author Sören Bernstein (quasimodo) quasi@zes.uni-bremen.de
 * @version $Id: CategorizationConfig.java 1169 2008-06-05 16:08:25Z quasimodo $
 */
public final class CategorizationConfig extends AbstractConfig {
    
    private static Logger s_log = Logger.getLogger(CategorizationConfig.class);

    private final Parameter m_showInternalName;

    /**
     * Public Constructor
     */
    public CategorizationConfig() {

        /**
         * If set to false for a given category id its label (name) is taken
         * from the language bundle for the language preference declared by
         * the browser (of the default, if the requested language is not supported).
         * If no language bundle exist for that id, the category will be
         * excluded from the category tree (or navigation tree is used in
         * navigation).
         */
        m_showInternalName = new BooleanParameter
            ("waf.categorization.show_internal_name", 
             Parameter.REQUIRED, 
             new Boolean(true));

        register(m_showInternalName);

        loadInfo();
    }

    /**
     * Returns the showInternalName flag.
     */
    public final boolean getShowInternalName() {
        return ((Boolean) get(m_showInternalName)).booleanValue();
    }
}
