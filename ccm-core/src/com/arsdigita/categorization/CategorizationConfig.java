/*
 * CategorizationConfig.java
 *
 * Created on 17. Januar 2008, 15:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.arsdigita.categorization;

/**
 *
 * @author quasi
 */

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.util.Assert;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.util.parameter.ErrorList;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ParameterError;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**
 * Stores the configuration record for the Categorization functionality
 */
public final class CategorizationConfig extends AbstractConfig {
    
    private static Logger s_log = Logger.getLogger(CategorizationConfig.class);

    private final Parameter m_showInternalName;
    private final Parameter m_supportedLanguages;

    public CategorizationConfig() {

        m_showInternalName = new BooleanParameter
            ("waf.categorization.show_internal_name", 
             Parameter.REQUIRED, 
             new Boolean(false));

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
