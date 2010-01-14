/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.search.intermedia;

import com.arsdigita.runtime.AbstractConfig;
// import com.arsdigita.runtime.CCMResourceManager;
// import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
// import com.arsdigita.util.parameter.StringParameter;

import org.apache.log4j.Logger;


/**
 * IntermediaConfig
 *
 * @author Peter Boy &lt;pboy@barkhof.uni-bremen.de&gt;
 * @version $Id: $
 **/

public class IntermediaConfig extends AbstractConfig {

    private static final Logger s_log = Logger.getLogger(IntermediaConfig.class);

    private static IntermediaConfig s_conf;

    static synchronized IntermediaConfig getConfig() {
        if (s_conf == null) {
            s_conf = new IntermediaConfig();
            s_conf.load();
        }

        return s_conf;
    }
    // see com.arsdigita.search.intermedia.BuildIndex.java for definitions.
    // All units are time in seconds!
    private IntegerParameter m_timerDelay = new IntegerParameter
        ("waf.intermedia.timer_delay", Parameter.REQUIRED,
        new Integer(60) );
    private IntegerParameter m_syncDelay = new IntegerParameter
        ("waf.intermedia.sync_delay", Parameter.REQUIRED,
        new Integer(60) );
    private IntegerParameter m_maxSyncDelay = new IntegerParameter
        ("waf.intermedia.max_sync_delay", Parameter.REQUIRED,
        new Integer(7200) );
    private IntegerParameter m_maxIndexingTime = new IntegerParameter
        ("waf.intermedia.max_indexing_time", Parameter.REQUIRED,
        new Integer(7200) );
    private IntegerParameter m_indexingRetryDelay = new IntegerParameter
        ("waf.intermedia.indexing_retry_delay", Parameter.REQUIRED,
        new Integer(60) );
    private BooleanParameter m_stemming = new BooleanParameter
	("waf.intermedia.stemming", Parameter.REQUIRED,
				    Boolean.FALSE);


    public IntermediaConfig() {
        register(m_timerDelay);
        register(m_syncDelay);
        register(m_maxSyncDelay);
        register(m_maxIndexingTime);
        register(m_indexingRetryDelay);
        register(m_stemming);
        
        loadInfo();
    }

    /**
     * Retrieve delay between triggering of search indexing Timer.
     * @return  delay, in seconds
     */
    public int getTimerDelay() {
        return ((Integer) get(m_timerDelay)).intValue();
    }

    /**
     * Retrieve time after which, if a content change is made, the index should 
     * be resynced if there are no other changes during that time.
     * @return  delay, in seconds
     */
    public int getSyncDelay() {
        return ((Integer) get(m_syncDelay)).intValue();
    }

    /*'
     * Retrieve time after which a change is made, the index will be resynced,
     * regardless of whether or not any changes have subsequently been made.
     * @return  delay, in seconds
     */
    public int getMaxSyncDelay() {
        return ((Integer) get(m_maxSyncDelay)).intValue();
    }

    /**
     * Retrieve time after which a indexing operation that has not finished is 
     * considered to have failed.
     * @return  time, in seconds
     */
    public int getMaxIndexingTime() {
        return ((Integer) get(m_maxIndexingTime)).intValue();
    }

    /**
     * Retrieve time after which an indexing operation that failed will be retried.
     * @return  delay, in seconds
     */
    public int getIndexingRetryDelay() {
        return ((Integer) get(m_indexingRetryDelay)).intValue();
    }

    /**
     * If intermedia is used, applies stemming operator to each individual word
     * in the contains clause. So for example a search for guitars will return
     * documents containing guitar. Search for ran returns documents containing
     * run.
     *
     * The stemming operator is not applied to compound phrases (included in
     * quotes by the user) as assume that user wants exact phrase. Also allows
     * user to override stemming by including single word in quotes
     *
     * @return
     */
    public boolean includeStemming() {
    	return ((Boolean)get(m_stemming)).booleanValue();

    }

}
