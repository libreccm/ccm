/*
 * Copyright (C) 2002-2005 Runtime Collective Ltd. All Rights Reserved.
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
 */
package com.arsdigita.aplaws;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.StringParameter;

import org.apache.log4j.Logger;

/**
 * AplawsConfig
 *
 * @version $Revision: 1.2 $ $Date: 2005/01/07 19:01:40 $
 * @version $Id: AplawsConfig.java 1565 2007-04-18 16:46:14Z apevec $
 */
public class AplawsConfig extends AbstractConfig {

    /** A logger instance.  */
    private static final Logger s_log = Logger.getLogger(AplawsConfig.class);

    /** Singelton config object.  */
    private static AplawsConfig s_conf;

    /**
     * Gain a SimpleAddressConfig object.
     *
     * Singelton pattern, don't instantiate a config object using the
     * constructor directly!
     * @return
     */
    public static synchronized AplawsConfig getConfig() {
        if (s_conf == null) {
            s_conf = new AplawsConfig();
            s_conf.load();
        }

        return s_conf;
    }

    // set of configuration parameters
    private final Parameter m_overrideAnavFromLGCLMappings =
            new BooleanParameter(
                "com.arsdigita.aplaws.override_anav_from_lgcl_mappings",
                Parameter.OPTIONAL, new Boolean(false));
    private final Parameter m_autocatServiceURL = 
            new StringParameter(
                "com.arsdigita.aplaws.autocat_url",
                Parameter.OPTIONAL,
                "http://demo.masprovider.com/searchLightWS/services/textMiner");
    private final Parameter m_autocatServiceUsername =
            new StringParameter(
                "com.arsdigita.aplaws.autocat_username",
                Parameter.OPTIONAL, null);
    private final Parameter m_autocatServicePassword =
            new StringParameter(
                "com.arsdigita.aplaws.autocat_password",
                Parameter.OPTIONAL, null);
    // moved into c.ad.london.terms.TermsConfig
    // private final Parameter m_ajaxExpandAllBranches =
    //         new BooleanParameter(
    //             "com.arsdigita.aplaws.ajax_expand_on_all_branches",
    //             Parameter.OPTIONAL, Boolean.FALSE);

    public AplawsConfig() {
        register(m_overrideAnavFromLGCLMappings);
        register(m_autocatServiceURL);
        register(m_autocatServiceUsername);
        register(m_autocatServicePassword);
        // moved into c.ad.london.terms.TermsConfig
        // register(m_ajaxExpandAllBranches);
        loadInfo();
    }

    public Boolean getOverrideAnavFromLGCLMappings() {
        return (Boolean) get(m_overrideAnavFromLGCLMappings);
    }

    public String getAutocatServiceURL() {
        String url = (String) get(m_autocatServiceURL);
        return url;
    }

    public String getAutocatServiceUsername() {
        String username = (String) get(m_autocatServiceUsername);
        return username;
    }

    public String getAutocatServicePassword() {
        String password = (String) get(m_autocatServicePassword);
        return password;
    }
//  moved into c.ad.london.terms.TermsConfig
//  public boolean ajaxExpandAllBranches () {
//  	return ((Boolean)get(m_ajaxExpandAllBranches)).booleanValue();
//  }
}
