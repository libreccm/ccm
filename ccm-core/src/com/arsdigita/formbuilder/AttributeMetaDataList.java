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
package com.arsdigita.formbuilder;


/**
 * Used to represent all attribute metadata of an
 * AttributeMetaDataProvider. Note this list will not function reliably
 * if it is shared by multiple threads. The way the Form Builder service
 * currently functions this never happens though.
 *
 * @author Peter Marklund
 * @version $Id: AttributeMetaDataList.java 287 2005-02-22 00:29:02Z sskracic $
 *
 */
public class AttributeMetaDataList {

    public static final String versionId = "$Id: AttributeMetaDataList.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private java.util.ArrayList m_attributeList;
    private int m_iteratorIndex = 0;

    public AttributeMetaDataList() {

        m_attributeList = new java.util.ArrayList();

    }

    public void add(AttributeMetaData attributeMetaData) {

        m_attributeList.add(attributeMetaData);
    }

    public boolean hasNext() {

        return m_attributeList.size() > m_iteratorIndex;
    }

    public AttributeMetaData next() {

        return (AttributeMetaData)m_attributeList.get(m_iteratorIndex++);
    }

    public void setIteratorAtStart() {

        m_iteratorIndex = 0;
    }

    /**
     * Retrieve the first AttributeMetaData object in the collection with
     * the given parameter name (the parameter name should be unique). Returns
     * null if there is no such parameter name.
     */
    public AttributeMetaData getByParameterName(String parameterName) {

        // Loop over the attributes and return the first attribute with
        // matching parameter name
        this.setIteratorAtStart();
        while (this.hasNext()) {
            AttributeMetaData attribute = this.next();

            if (attribute.getParameterName().equals(parameterName)) {
                return attribute;
            }
        }

        // No matching parameter name found
        return null;
    }

    public int size() {
        return m_attributeList.size();
    }
}
