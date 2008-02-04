/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.bebop.jsp;

/**
 * Interface class for JSP constants.
 */
interface JSPConstants {
    public static final String versionId = "$Id: JSPConstants.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    final static String BEBOP_XMLNS =
        "http://www.arsdigita.com/bebop/1.0";

    final static String SLAVE_DOC =
        "com.arsdigita.bebop.jsp.SlaveDocument";

    final static String SLAVE_INPUT_DOC =
        "com.arsdigita.bebop.jsp.SlaveInputDocument";

    final static String INPUT_DOC_ATTRIBUTE =
        "com.arsdigita.xml.Document";

    final static String INPUT_PAGE_STATE_ATTRIBUTE =
        "pageState";
}
