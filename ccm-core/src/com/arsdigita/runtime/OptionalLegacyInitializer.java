/*
 * Copyright (C) 2004-2005 Runtime Collective Ltd. All Rights Reserved.
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
package com.arsdigita.runtime;

import java.io.InputStream;

/**
 * The OptionalLegacyInitializer behaves like LegacyInitializer,
 * except that it won't break if the configuration file cannot
 * be found.
 **/
public class OptionalLegacyInitializer extends LegacyInitializer {

    public static final String versionId = "$Id: OptionalLegacyInitializer.java 738 2005-09-01 12:36:52Z sskracic $";

    public OptionalLegacyInitializer(String init, ClassLoader loader) {
        super(init, loader);
    }

    public OptionalLegacyInitializer(String init) {
        super(init);
    }

    /** Don't throw an exception if the file is not there. */
    public void init(LegacyInitEvent evt) {
        InputStream is = m_reg.load(m_init);
        if (is == null) {
            //throw new IllegalStateException("no such resource: " + m_init);
        } else {
	    super.init(evt);
	}
    }
}
