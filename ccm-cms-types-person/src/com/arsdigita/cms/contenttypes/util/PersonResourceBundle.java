/*
 * Copyright (C) 2009 Jens Pelzetter, for the Center of Social Politics of the University of Bremen
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
import com.arsdigita.cms.util.CMSGlobalized;

public class PersonResourceBundle extends ChainedResourceBundle implements CMSGlobalized {
    public static final String PERSON_BUNDLE_NAME =
	"com.arsdigita.cms.contenttypes.PersonResources";

    public PersonResourceBundle() {
	super();
	addBundle((PropertyResourceBundle)getBundle(PERSON_BUNDLE_NAME));
	addBundle((PropertyResourceBundle)getBundle(BUNDLE_NAME));
    }
}