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
// unused imports
// import com.arsdigita.runtime.RuntimeConfig;
// import com.arsdigita.util.Assert;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.StringParameter;
// import com.arsdigita.util.parameter.ErrorList;
// import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
// import com.arsdigita.util.parameter.ParameterError;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

// import java.util.Map;
// import java.util.HashMap;
// import java.util.Set;
// import java.util.HashSet;

/**
 * Stores the configuration record for the Categorization functionality.
 *
 * @author Sören Bernstein (quasimodo) quasi@zes.uni-bremen.de
 */
public final class CategorizationConfig extends AbstractConfig {
    public static final String versionId =
        "$Id: CategorizationConfig.java 1169 2008-06-05 16:08:25Z quasimodo $" +
        "$Author: quasimodo $" +
        "$DateTime: 2008/06/05 16:08:25 $";
    
    private static Logger s_log = Logger.getLogger(CategorizationConfig.class);

    private final Parameter m_showInternalName;
    private final Parameter m_supportedLanguages;

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

        /**
         * String containing the supported languages. The first one is considered
         * as default.
         */
        m_supportedLanguages = new StringParameter
            ("waf.categorization.supported_languages", 
             Parameter.REQUIRED, 
             "en,de,fr");

        register(m_showInternalName);
        register(m_supportedLanguages);

        loadInfo();
    }

    /**
     * Returns the showInternalName flag.
     */
    public final boolean getShowInternalName() {
        return ((Boolean) get(m_showInternalName)).booleanValue();
    }

    /**
     * Returns the defaultLanguage flag.
     */
    public final String getDefaultLanguage() {
        return ((String) get(m_supportedLanguages)).trim().substring(0, 2);
    }

    /**
     * Returns the supportedLanguages as StringTokenizer.
     */
    public final StringTokenizer getSupportedLanguages() {
        return new StringTokenizer((String) get(m_supportedLanguages), ",", false);
    }
    
    /**
     * Return true, if language lang is part of supported langs
     */
    public final boolean hasLanguage(String lang) {
        return ((String) get(m_supportedLanguages)).contains(lang);
    }

}
