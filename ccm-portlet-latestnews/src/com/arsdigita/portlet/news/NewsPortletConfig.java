/*
 * Copyright (C) 2003 - 2004 Chris Gilbert  All Rights Reserved.
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

package com.arsdigita.portlet.news;

import com.arsdigita.runtime.AbstractConfig;
// import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
// import com.arsdigita.util.parameter.StringArrayParameter;
// import com.arsdigita.util.parameter.URLParameter;
import com.arsdigita.util.parameter.StringParameter;
// import com.arsdigita.util.UncheckedWrapperException;

// import java.io.InputStream;
// import java.io.IOException;
// import java.net.URL;
// import java.net.MalformedURLException;
import org.apache.log4j.Logger;

/**
 * Specification of datasources required by Atomwide application
 *
 * @author Chris Gilbert &lt;chris.gilbert@westsussex.gov.uk&gt;
 * @version $Id: NewsPortletConfig.java,v 1.1 2005/03/07 13:48:49 cgyg9330 Exp $
 */
public class NewsPortletConfig extends AbstractConfig {

    /** A logger instance to assist debugging.  */
    private static final Logger s_log = Logger.getLogger(NewsPortletConfig.class);

    /** Singelton config object.  */
    private static NewsPortletConfig s_conf;

    /**
     * Gain a NewsPortletConfig object.
     *
     * Singelton pattern, don't instantiate a config object using the
     * constructor directly!
     * use RSSFeedConfig.getConfig(); instead
     * @return
     */
    public static synchronized NewsPortletConfig getConfig() {
        if (s_conf == null) {
            s_conf = new NewsPortletConfig();
            s_conf.load();
        }

        return s_conf;
    }
    

    // //////////////////////////////////////////////////////////////////////// 
    // Set of configuration parameters
			
    /**  */
    private final Parameter newsroomShortcut = new StringParameter
			( "com.arsdigita.portlet.news.newsroom-shortcut",
					Parameter.REQUIRED,
					"/news");

    /**
     * Constructor initializes class.
     */
    public NewsPortletConfig() {

        register(newsroomShortcut);
       	loadInfo();
    }

    
    /**
     * Getter newsroom shortcut parameter
     */
    public final String getNewsroomShortcut() {
        return (String)get(newsroomShortcut);
    }
  
}
