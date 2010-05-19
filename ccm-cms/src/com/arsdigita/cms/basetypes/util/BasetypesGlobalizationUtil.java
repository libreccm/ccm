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

package com.arsdigita.cms.basetypes.util;

import com.arsdigita.globalization.GlobalizedMessage;

public class BasetypesGlobalizationUtil {
    final public static String BUNDLE_NAME = 
	"com.arsdigita.cms.basetypes.util.BaetypesResourceBundle";

    public static GlobalizedMessage globalize (String key) {
	return new GlobalizedMessage(key, BUNDLE_NAME);
    }

    public static GlobalizedMessage globalize (String key, Object[] args) {
	return new GlobalizedMessage(key, BUNDLE_NAME, args);
    }
}