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
package com.arsdigita.loader;

import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ParameterRecord;
import com.arsdigita.util.parameter.StringParameter;

/**
 * MimeTypeRow
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/08/16 $
 **/

class MimeTypeRow extends ParameterRecord {

    public final static String versionId = "$Id: MimeTypeRow.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private StringParameter m_type = new StringParameter
        ("waf.mime.type", Parameter.REQUIRED, null);

    private StringParameter m_label = new StringParameter
        ("waf.mime.label", Parameter.REQUIRED, null);

    private StringParameter m_extensions = new StringParameter
        ("waf.mime.extensions", Parameter.REQUIRED, null);

    private StringParameter m_objectType = new StringParameter
        ("waf.mime.object_type", Parameter.REQUIRED, null);

    private StringParameter m_sizerOrINSO = new StringParameter
        ("waf.mime.sizer_or_inso", Parameter.OPTIONAL, null);

    public MimeTypeRow() {
        super("mime-type-row");
        register(m_type);
        register(m_label);
        register(m_extensions);
        register(m_objectType);
        register(m_sizerOrINSO);
    }

    public String getType() {
        return (String) get(m_type);
    }

    public String getLabel() {
        return (String) get(m_label);
    }

    public String getExtensions() {
        return (String) get(m_extensions);
    }

    /**
     * Get default Extension (first one in list)
     **/

    public String getDefaultExtension() {
        String exts = getExtensions();
        int index = exts.indexOf(',', 0);
        if (index < 0) {
            return exts;
        } else {
            return exts.substring(0, index);
        }
    }

    private String typeName() {
        return (String) get(m_objectType);
    }

    public String getObjectType() {
        return "com.arsdigita.cms." + typeName();
    }

    public String getJavaClass() {
        return "com.arsdigita.mimetypes." + typeName();
    }

    public String getSizerOrINSO() {
        return (String) get(m_sizerOrINSO);
    }

}
