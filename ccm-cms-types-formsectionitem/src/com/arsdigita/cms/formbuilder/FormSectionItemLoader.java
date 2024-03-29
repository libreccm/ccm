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
package com.arsdigita.cms.formbuilder;

import com.arsdigita.cms.contenttypes.AbstractContentTypeLoader;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Loader
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #9 $ $Date: 2004/08/17 $
 * @version $Id: FormSectionItemLoader.java 755 2005-09-02 13:42:47Z sskracic $
 *
 */
public class FormSectionItemLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/formbuilder/FormSectionItem.xml"
    };
    /**
     * List of content sections to install FormSection. An empty list installs Forms into the default (first) content
     * section only.
     */
    private final Parameter m_contentSections = new StringParameter(
            "com.arsdigita.cms.formbuilder.FormSectionItem.sections",
            Parameter.REQUIRED, "");

    {
        register(m_contentSections);
        loadInfo();
    }

    @Override
    public String[] getTypes() {
        return TYPES;
    }

    /**
     * Overwrites parents class's method to predefine into which content sections Forms should get installed. An empty
     * list (the default parents's implementation) gets Forms installed into the default section only.
     *
     * @return Array of content sections Forms should be installed into
     */
    @Override
    public List getContentSections() {
        List result = new ArrayList();
        if (!(get(m_contentSections).toString() != null)
            && !get(m_contentSections).toString().isEmpty()) {
            result.add(get(m_contentSections));
        }
        return result;
    }
}
