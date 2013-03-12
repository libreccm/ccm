/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes.util;

import com.arsdigita.cms.util.CMSGlobalized;
import com.arsdigita.globalization.ChainedResourceBundle;

import java.util.PropertyResourceBundle;

/**
 * Resource Bundle used in UI for Event ContentType.
 * 
 * @author Shashin Shinde (sshinde@redhat.com)
 */
public class EventResourceBundle extends ChainedResourceBundle 
                                   implements CMSGlobalized {
    
    public final static String EVENT_BUNDLE_NAME = 
        "com.arsdigita.cms.contenttypes.EventResources";

    public EventResourceBundle() {
        super();
        addBundle((PropertyResourceBundle)getBundle(EVENT_BUNDLE_NAME));
        addBundle((PropertyResourceBundle)getBundle(BUNDLE_NAME));
    }
}
