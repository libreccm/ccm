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
package com.arsdigita.forum.ui;

import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Contains methods to simplify globalizing keys.
 *
 * @author Daniel Berrange
 */
public class Text {

    private static final String BUNDLE_NAME = 
        "com.arsdigita.forum.ui.ForumResources";

    public static GlobalizedMessage gz(String key) {
        return new GlobalizedMessage(key, BUNDLE_NAME);
    }

    public static String gzAsStr(String key) {
        return (String) new GlobalizedMessage(key, BUNDLE_NAME).localize();
    }

    public static GlobalizedMessage gz(String key, Object[] args) {
        return new GlobalizedMessage(key, BUNDLE_NAME, args);
    }

}
