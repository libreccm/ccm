/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.london.search;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.StringArrayParameter;
import com.arsdigita.util.parameter.StringParameter;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;


/**
 * A record containing search configuration properties.
 *
 * @version $Id: SearchConfig.java 582 2005-06-16 12:51:16Z fabrice $
 */
public final class SearchConfig extends AbstractConfig {

    /** A logger instance to assist debugging.  */
    private static final Logger s_log = Logger.getLogger(SearchConfig.class);

    /** Singelton config object.  */
    private static SearchConfig s_conf;

    /**
     * Gain a SearchConfig object.
     *
     * Singelton pattern, don't instantiate a config object using the
     * constructor directly!
     * @return
     */
    public static synchronized SearchConfig getConfig() {
        if (s_conf == null) {
            s_conf = new SearchConfig();
            s_conf.load();
        }

        return s_conf;
    }


    // //////////////////////////////////////////////////////////////////////// 
    // Set of configuration parameters

    /** 
     * Sets the number of threads that can respond to remote search queries
     */
    private final Parameter m_numThreads = new 
            IntegerParameter("com.arsdigita.london.search.num_threads", 
                             Parameter.REQUIRED, new Integer(10));
    /**
     * Sets the number of milleseconds to wait for the search to return results 
     * before timing out
     */
    private final Parameter m_searchTimeout = new 
            IntegerParameter("com.arsdigita.london.search.timeout", 
                             Parameter.REQUIRED, new Integer(4000));
    /**
     * Whether or not to display Sponsored Links in addition to search results
     */
    private final Parameter m_showSponsoredLinks = new 
            BooleanParameter("com.arsdigita.london.search.show_sponsored_links",
                             Parameter.REQUIRED, Boolean.FALSE);
    /**
     * What is the maximum number of remote search results this server should return
     */
    private final Parameter m_maxRemoteResults = new 
            IntegerParameter("com.arsdigita.london.search.max_remote_results",
                             Parameter.REQUIRED, new Integer(50));
    /**
     * When this host is a target for remote search, filter results to 
     * specified content sections
     */
    private final Parameter m_remoteSearchContentSections = new 
            StringArrayParameter("com.arsdigita.london.search.remote_search_content_sections",
                                 Parameter.OPTIONAL, null);
    /**
     * 
     */
    private final Parameter m_simpleRestrictTo = new 
            StringParameter("com.arsdigita.london.search.simple_restrict_to",
                            Parameter.OPTIONAL, "");
    
    private String[] simpleRestrictToArray;

    /**
     * Constructor registers the configuration parameters and reads values from
     * registry file.
     */
    public SearchConfig() {

        register(m_numThreads);
        register(m_searchTimeout);
        register(m_showSponsoredLinks);
        register(m_maxRemoteResults);
        register(m_remoteSearchContentSections);
        register(m_simpleRestrictTo);

        loadInfo();

    }


    /**
     * Get the number of threads that can respond to remote search queries
     * @return Number of threads to start
     */
    public final Integer getNumberOfThreads() {
        return (Integer) get(m_numThreads);
    }


    /**
     * Get the number of milleseconds to wait for the search to return results 
     * before timing out
     * @return Milliseconds to wait
     */
    public final Integer getSearchTimeout() {
        return (Integer) get(m_searchTimeout);
    }

    /**
     * Whether or not to display Sponsored Links in addition to search results
     * @return 
     */
    public final Boolean getShowSponsoredLinks() {
        return (Boolean) get(m_showSponsoredLinks);
    }

    /**
     * Get the maximum number of remote search results this server should return
     * @return No of search results to show 
     */
    public final Integer getMaxRemoteResults() {
        return (Integer) get(m_maxRemoteResults);
    }

    /**
     * When this server is the target of a remote search, this
     * parameter enables us to filter by content section - so for
     * example, we can prevent results from subsites being
     * available externally is we specify the main site content
     * section. If parameter is unset, then all content sections
     * are searched
     * @return
     */
    public final String[] getRemoteSearchContentSections() {
        return (String[])get(m_remoteSearchContentSections);
    }

    public final String getSimpleRestrictTo() {
        return (String) get(m_simpleRestrictTo);
    }

    public final String[] getSimpleRestrictToArray() {
        if (simpleRestrictToArray == null) {
            loadSimpleRestrictToArray();
        }
        return simpleRestrictToArray;
    }

    protected void loadSimpleRestrictToArray() {
        s_log.info("Restricting to "+getSimpleRestrictTo());
        StringTokenizer st = new StringTokenizer(getSimpleRestrictTo(), ",");
        simpleRestrictToArray = new String[st.countTokens()];
        s_log.info("Parsing "+st.countTokens()+" content types.");
        int index = 0;
        while (st.hasMoreTokens()) {
            simpleRestrictToArray[index] = st.nextToken().trim();
            s_log.info("Restricting to : "+simpleRestrictToArray[index]);
            index++;
        }
    }
}
