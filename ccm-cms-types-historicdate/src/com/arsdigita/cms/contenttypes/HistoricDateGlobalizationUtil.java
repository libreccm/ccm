/*
 * Copyright (C) 2014 Jens Pelzetter All Rights Reserved.
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

package com.arsdigita.cms.contenttypes;

import com.arsdigita.globalization.GlobalizedMessage;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class HistoricDateGlobalizationUtil {
    
    public final static String BUNDLE_NAME = "com.arsdigita.cms.contenttypes.HistoricDateResources";
    
    
    public static GlobalizedMessage globalize(final String key) {
        return new GlobalizedMessage(key, BUNDLE_NAME);
    }
    
    public static GlobalizedMessage globalize(final String key, final Object[] args) {
        return new GlobalizedMessage(key, BUNDLE_NAME, args);
    }
}
