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
 */

package com.arsdigita.cms.contenttypes.xmlfeed;

import com.arsdigita.cms.contenttypes.ContentTypeInitializer;

/**
 * The XML Feed initializer.
 *
 * @version $Id: Initializer.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class Initializer extends ContentTypeInitializer {
    public final static String versionId =
        "$Id: Initializer.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/02/06 11:50:22 $";

    public Initializer() {
        super("ccm-cms-types-xmlfeed.pdl.mf",
              XMLFeed.BASE_DATA_OBJECT_TYPE);
    }

    public String[] getStylesheets() {
        return new String[] { "/static/content-types/com/" + 
                              "arsdigita/cms/contenttypes/XMLFeed.xsl" };
    }

}
