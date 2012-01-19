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

package com.arsdigita.rssfeed;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ParameterError;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.util.parameter.ErrorList;
import org.apache.log4j.Logger;

/**
 *  The file that contains all configuration information for
 *  the RSSFeed application.
 *  @version $Id: RSSFeedConfig.java 1319 2006-09-15 10:52:49Z apevec $
 */
public final class RSSFeedConfig extends AbstractConfig {

    /** A logger instance to assist debugging.  */
    private static final Logger s_log = Logger.getLogger(RSSFeedConfig.class);

    /** Singelton config object.  */
    private static RSSFeedConfig s_conf;

    /**
     * Gain a RSSFeedConfig object.
     *
     * Singelton pattern, don't instantiate a config object using the
     * constructor directly!
     * use RSSFeedConfig.getConfig(); instead
     * @return
     */
    public static synchronized RSSFeedConfig getConfig() {
        if (s_conf == null) {
            s_conf = new RSSFeedConfig();
            s_conf.load();
        }

        return s_conf;
    }

    // //////////////////////////////////////////////////////////////////////// 
    // Set of configuration parameters

    /**  */
    private final Parameter m_categoryKey= new RSSCategoryKeyParameter
            ("com.arsdigita.rssfeed.categoryKey", Parameter.REQUIRED, "RSS");
    /** */
    private final Parameter m_processingInstruction_xslt= new StringParameter
            ("com.arsdigita.rssfeed.processingInstruction_xslt",
             Parameter.OPTIONAL, null);

    /**
     * Constructor initializes class.
     */
    public RSSFeedConfig() {

        register(m_categoryKey);
        register(m_processingInstruction_xslt);

        loadInfo();
    }


    public String getCategoryKey() {
        return (String) get(m_categoryKey);
    }

    public String getPIxslt() {
        return (String) get(m_processingInstruction_xslt);
    }

    private static class RSSCategoryKeyParameter
        extends StringParameter {
        RSSCategoryKeyParameter(final String name,
                                final int multiplicity,
                                final Object defaalt) {
            super(name, multiplicity, defaalt);
        }

        @Override
        public void doValidate(final Object value, final ErrorList errors) {
            if (value != null) {
                String key = (String)value;
                for (int i = 0; i < key.length(); i++) {
                    char j = key.charAt(i);
                    if (Character.isWhitespace(j)) {
                        errors.add(new ParameterError(
                                       this,
                                       "The value of " + getName() + " must not " +
                                       " contain any white space"));
                        break;
                    }
                }
            } else {
                errors.add(new ParameterError(
                               this, 
                               "The value of " + getName() + " must not be null."));
            }
        }
    }
}
