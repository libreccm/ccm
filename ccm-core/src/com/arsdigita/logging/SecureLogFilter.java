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
 *
 */
package com.arsdigita.logging;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * <p>
 * Defines a log4j filter that will filter out any messages that
 * are tagged as secure by the Log class.
 * </p>
 *
 * @author Yon Feldman 
 * @version $Id: SecureLogFilter.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class SecureLogFilter extends Filter {
    /**
     * Decides whether to let this log message go through.
     *
     * @param event a LoggingEvent to decide about letting go through
     */
    public int decide(LoggingEvent event) {
        // in here we have to check whether secure logging is on or off.
        if(Log.isSecure())
            return Filter.DENY;
        return Filter.NEUTRAL;
    }

    /**
     * Filter that does not support any options.
     */
    public String[] getOptionStrings() {
        return new String[] {"", ""};
    }

    /**
     * Filter that does not support any options.
     */
    public void setOption(String key, String value) {}
}
