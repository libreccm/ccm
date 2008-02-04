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
package com.arsdigita.templating;

import com.arsdigita.util.servlet.HttpHost;
import com.arsdigita.web.Web;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;


/**
 * Generates a set of patterns corresponding to the current 
 * web application prefix
 */
public class HostPatternGenerator implements PatternGenerator {

    private static final Logger s_log = 
         Logger.getLogger(URLPatternGenerator.class);

    public String[] generateValues(String key,
                                   HttpServletRequest req) {
        HttpHost host = Web.getConfig().getHost();
        
        return new String[] { host.toString() };
    }
}
