/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.categorization.util;

import com.arsdigita.globalization.Globalized;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Contains methods to simplify globalizing keys.
 *
 * @version $Revision: #7 $ $Date: 2004/08/16 $
 */
public class GlobalizationUtil implements Globalized {
    
    private static final String BUNDLE_NAME =
        "com.arsdigita.categorization.CategorizationResources";

    private GlobalizationUtil() {}

    public static GlobalizedMessage globalize(String key) {
        return new GlobalizedMessage(key, BUNDLE_NAME);
    }

    public static GlobalizedMessage globalize(String key, Object[] args) {
        return new GlobalizedMessage(key, BUNDLE_NAME, args);
    }
}
