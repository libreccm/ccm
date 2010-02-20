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

import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;

import com.arsdigita.cms.contenttypes.AbstractContentTypeLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * XML Feed Loader.
 *
 * @version $Id: Loader.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class Loader extends AbstractContentTypeLoader {

    private final Parameter m_contentSections = new StringParameter
        ("com.arsdigita.cms.contenttypes.xmlfeed.sections",
         Parameter.REQUIRED, "forms");

    public Loader() {
        register(m_contentSections);
        loadInfo();
    }

    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/XMLFeed.xml"
    };

    public String[] getTypes() {
        return TYPES;
    }
    
    public List getContentSections() {
        List result = new ArrayList(1);
        result.add(get(m_contentSections));
        return result;
    }

}
