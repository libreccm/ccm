/*
 * Copyright (C) 2014 Peter Boy, Universitaet Bremen. All Rights Reserved.
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

package com.arsdigita.portal;

import javax.portlet.GenericPortlet;

import org.apache.log4j.Logger;

/**
 * Currently a kind of wrapper class to enable CCM to deliver its portlets
 * to JSR 286 compliant portal server.
 * 
 * Currentliy WORK IN PROGRESS!
 * 
 * @author pb
 */
public class JSRPortlet extends GenericPortlet {

    /** Internal logger instance to faciliate debugging. Enable logging output
     *  by editing /WEB-INF/conf/log4j.properties int the runtime environment
     *  and set com.arsdigita.portal.JSRPortlet=DEBUG 
     *  by uncommenting or adding the line.                                                   */
    private static final Logger s_log = Logger.getLogger(JSRPortlet.class);

    
}
