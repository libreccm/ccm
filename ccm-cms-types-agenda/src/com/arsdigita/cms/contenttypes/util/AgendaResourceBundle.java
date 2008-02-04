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

import java.util.PropertyResourceBundle;
import com.arsdigita.globalization.ChainedResourceBundle;
import com.arsdigita.cms.CMSGlobalized;

/**
 * Form to edit the basic properties of an event. This form can be extended to
 * create forms for Event subclasses.
 **/
public class AgendaResourceBundle extends ChainedResourceBundle implements CMSGlobalized {
    
    public final static String AGENDA_BUNDLE_NAME = 
        "com.arsdigita.cms.contenttypes.AgendaResources";

    public AgendaResourceBundle() {
        super();
        addBundle((PropertyResourceBundle)getBundle(AGENDA_BUNDLE_NAME));
        addBundle((PropertyResourceBundle)getBundle(BUNDLE_NAME));
    }
}
