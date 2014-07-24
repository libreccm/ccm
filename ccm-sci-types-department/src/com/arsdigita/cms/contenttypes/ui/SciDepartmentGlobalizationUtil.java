/*
 * Copyright (C) 2012 Jens Pelzetter. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Compilation of methods to simplify the handling of globalizing keys.
 * Basically it adds the name of package's resource bundle files to the
 * globalize methods and forwards to GlobalizedMessage, shortening the
 * method invocation in the various application classes.
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartmentGlobalizationUtil {

    /**  Name of Java resource files to handle SciDepartment's globalisation. */
    public static final String BUNDLE_NAME =
                  "com.arsdigita.cms.contenttypes.SciDepartmentResources";

    /**
     * Returns a globalized message using the appropriate bundle.
     * If the key string contains the modules name agenda the package specific
     * bundle is used, otherwise the CMS ResourceBundle.
     */
    public static GlobalizedMessage globalize(final String key) {
        return new GlobalizedMessage(key, BUNDLE_NAME);
    }

    /**
     * Returns a globalized message object, using the approprate bundle,
     * takeing in an Object[] of arguments to interpolate into the retrieved 
     * message using the  MessageFormat class.
     * If the key string contains the modules name agenda the package specific
     * bundle is used, otherwise the CMS ResourceBundle.
     */
    public static GlobalizedMessage globalize(final String key,
                                              final Object[] args) {
        return new GlobalizedMessage(key, BUNDLE_NAME, args);
    }
}
