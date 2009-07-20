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
package com.arsdigita.cms.contenttypes;

import org.apache.log4j.Logger;

/**
 * The CMS initializer.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: OrganizationInitializer.java 757 2005-09-02 14:12:21Z sskracic $
 */
public class BaseContactInitializer extends ContentTypeInitializer {
    public final static String versionId =
            "$Id: BaseContactInitializer.java $" +
            "$Author: quasi $" +
            "$DateTime: 2009/03/15 $";
    private static final Logger s_log = Logger.getLogger(BaseContactInitializer.class);
    
    public BaseContactInitializer() {
        super("ccm-cms-types-baseContact.pdl.mf",
                BaseContact.BASE_DATA_OBJECT_TYPE);
    }
    
    public String[] getStylesheets() {
        return new String[] {
            "/static/content-types/com/arsdigita/cms/contenttypes/BaseContact.xsl"
        };
    }
    
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/BaseContact.xml";
    }
    
}
