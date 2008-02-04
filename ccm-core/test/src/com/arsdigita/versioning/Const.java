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
package com.arsdigita.versioning;

public interface Const {
    String MARKED      = " is marked versioned";
    String UNMARKED    = " is not marked versioned";
    String UNVERSIONED = " is marked unversioned";
    String VERSIONED_TYPE      = " is a versioned type";
    String COVERSIONED_TYPE    = " is a coversioned type";
    String RECOVERABLE         = " is recoverable";
    String UNREACHABLE         = " is unreachable";


    String EVENT_PROC_MODEL = "versioning.events";
    // object types
    String C1    = EVENT_PROC_MODEL + ".C1";
    String C2    = EVENT_PROC_MODEL + ".C2";
    String RET1  = EVENT_PROC_MODEL + ".RET1";
    String RT1   = EVENT_PROC_MODEL + ".RT1";
    String UT1   = EVENT_PROC_MODEL + ".UT1";
    String UT2   = EVENT_PROC_MODEL + ".UT2";
    String UVCT1 = EVENT_PROC_MODEL + ".UVCT1";
    String UVCT2 = EVENT_PROC_MODEL + ".UVCT2";
    String VT1   = EVENT_PROC_MODEL + ".VT1";
    String VT1E  = EVENT_PROC_MODEL + ".VT1E";
    String VT2   = EVENT_PROC_MODEL + ".VT2";
    String VT3   = EVENT_PROC_MODEL + ".VT3";
    String VT4   = EVENT_PROC_MODEL + ".VT4";
    String VT5   = EVENT_PROC_MODEL + ".VT5";
    String VUT1  = EVENT_PROC_MODEL + ".VUT1";

    // attribute names
    String C1S        = "c1s";
    String C2_ATTR    = "c2";
    String CONTENT    = "content";
    String ID         = "id";
    String INT_ATTR   = "intAttr";
    String NAME       = "name";
    String RET1S      = "ret1s";
    String RT1_ATTR   = "rt1";
    String UNVER_ATTR = "unverAttr";
    String UVCT1S     = "uvct1s";
    String UVCT2S     = "uvct2s";
    String UT2S       = "ut2s";
    String VT1E_ATTR  = "vt1eAttr";
    String VUT1_ATTR  = "vut1Attr";
}
