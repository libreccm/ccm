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
package com.arsdigita.search.lucene;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainServiceInterfaceExposer;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.StringUtils;
import com.arsdigita.kernel.Party;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Date;


/**
 * StandardAdapter
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2004/08/16 $
 **/

public class StandardAdapter extends Adapter {

    private ObjectType m_type;
    private Property m_id;
    private Property m_title;
    private Property m_summary;
    private Property[] m_content;

    public StandardAdapter(ObjectType type,
                           Property id,
                           Property title,
                           Property summary,
                           Property[] content) {
        m_type = type;
        m_id = id;
        m_title = title;
        m_summary = summary;
        m_content = content;
    }

    public StandardAdapter(ObjectType type,
                           String id,
                           String title,
                           String summary,
                           String[] content) {
        this(type, getProperty(type, id), getProperty(type, title),
             getProperty(type, summary), getProperties(type, content));
    }

    private static final Property getProperty(ObjectType type, String prop) {
        if (prop == null) {
            return null;
        } else {
            return type.getProperty(prop);
        }
    }

    private static final Property[] getProperties(ObjectType type,
                                                  String[] props) {
        Property[] result = new Property[props.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = type.getProperty(props[i]);
        }

        return result;
    }

    public StandardAdapter(String type,
                           String id,
                           String title,
                           String summary,
                           String[] content) {
        this(MetadataRoot.getMetadataRoot().getObjectType(type),
             id,
             title,
             summary,
             content);
    }

    public BigDecimal getID(DomainObject dobj) {
        return (BigDecimal) get(dobj, m_id);
    }

    public ObjectType getObjectType(DomainObject dobj) {
        return dobj.getObjectType();
    }

    public String getTypeSpecificInfo(DomainObject dobj) {
        return null;
    }

    public Locale getLocale(DomainObject dobj) {
        return null;
    }

    public String getTitle(DomainObject dobj) {
        return getString(dobj, m_title);
    }

    public String getSummary(DomainObject dobj) {
        return getString(dobj, m_summary);
    }

    public String getContent(DomainObject dobj) {
        StringBuffer result = new StringBuffer();

        boolean first = true;
        for (int i = 0; i < m_content.length; i++) {
            String str = getString(dobj, m_content[i]);
            if (str != null) {
                if (first) {
                    first = false;
                } else {
                    result.append(" ");
                }
                result.append(str);
            }
        }

        if (first) {
            return null;
        } else {
            String content = result.toString().replace('\n', ' ');
            return StringUtils.htmlToText(content);
        }
    }

    public Date getCreationDate(DomainObject dobj) {
        return null;
    }

    public Party getCreationParty(DomainObject dobj) {
        return null;
    }

    public Date getLastModifiedDate(DomainObject dobj) {
        return null;
    }

    public Party getLastModifiedParty(DomainObject dobj) {
        return null;
    }

    private static final String getString(DomainObject dobj, Property prop) {
        if (prop == null) {
            return null;
        }

        Object result = get(dobj, prop);
        if (result == null) {
            return null;
        } else {
            return result.toString();
        }
    }

    private static final Object get(DomainObject dobj, Property prop) {
        return getDataObject(dobj).get(prop.getName());
    }

    private static final DataObject getDataObject(DomainObject dobj) {
        return DomainServiceInterfaceExposer.getDataObject(dobj);
    }

}
