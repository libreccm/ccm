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
package com.arsdigita.cms.lifecycle;

import com.arsdigita.runtime.AbstractConfig;
// import com.arsdigita.runtime.CCMResourceManager;
// import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
// import com.arsdigita.util.parameter.StringParameter;

import org.apache.log4j.Logger;

/**
 * LifecycleConfig
 *
 * @author Peter Boy &lt;pboy@barkhof.uni-bremen.de&gt;
 * @version $Id: $
 */
public class LifecycleConfig extends AbstractConfig {

    private static final Logger s_log = Logger.getLogger(LifecycleConfig.class);
    private static LifecycleConfig s_conf;

    /**
     * Gain a LifecycleConfig object.
     * 
     * Singelton pattern, don't instantiate a lifecacle object using the 
     * constructor directly.
     * @return
     */
    static synchronized LifecycleConfig getConfig() {
        if (s_conf == null) {
            s_conf = new LifecycleConfig();
            s_conf.load();
        }

        return s_conf;
    }
    /**
     * How long do we wait (in seconds) after system startup before we start
     * processing lifecycles?
     */
    private IntegerParameter m_delay = new IntegerParameter(
            "com.arsdigita.cms.lifecycle.delay", Parameter.REQUIRED,
            new Integer(60));
    /**
     * How often (in seconds) does the system look for pending items to make
     * live and live items to expire? A value of 0 disables LC background thread.
     */
    private IntegerParameter m_frequency =
                             new IntegerParameter(
            "com.arsdigita.cms.lifecycle.frequency", Parameter.REQUIRED,
            new Integer(600));
    
    /**
     * Constructor.
     * Do not use it directly!
     */
    public LifecycleConfig() {
        register(m_delay);
        register(m_frequency);      

        loadInfo();
    }

    /**
     * Retrieve delay between triggering lifecycle timer and system startup.
     * @return  delay, in seconds. A value of 0 disables LC background thread.
     */
    public int getDelay() {
        s_log.debug("delay time retrieved.");
        return ((Integer) get(m_delay)).intValue();
    }

    /**
     * Retrieve frequency to  look for pending items to make live or expire.
     * @return  delay, in seconds
     */
    public int getFrequency() {
        s_log.debug("frequency time retrieved.");
        return ((Integer) get(m_frequency)).intValue();
    }
   
}
