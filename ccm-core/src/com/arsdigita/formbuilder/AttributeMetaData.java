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


import com.arsdigita.bebop.parameters.ParameterModel;

import com.arsdigita.formbuilder.util.FormBuilderUtil;


/**
 * Represents the metadata of one attribute of a <code>AttributeMetaDataProvider</code>.
 *
 * @author Peter Marklund
 * @version $Id: AttributeMetaData.java 287 2005-02-22 00:29:02Z sskracic $
 *
 */
public class AttributeMetaData {


    private String m_parameterName;
    private String m_label;
    private boolean m_isRequired = false;
    private boolean m_isMultiple = false;
    private AttributeType m_attributeType;


    //*** Constructors

    public AttributeMetaData(String parameterName) {

        this(parameterName, null, false, false, null);
    }

    public AttributeMetaData(String parameterName, boolean isRequired) {

        this(parameterName, null, isRequired, false, null);
    }

    public AttributeMetaData(String parameterName, boolean isRequired, boolean isMultiple) {

        this(parameterName, null, isRequired, isMultiple, null);
    }

    public AttributeMetaData(String parameterName, AttributeType attributeType) {

        this(parameterName, null, false, false, attributeType);
    }

    public AttributeMetaData(String parameterName, String label) {

        this(parameterName, label, false, false, null);
    }

    public AttributeMetaData(String parameterName, String label, boolean isRequired) {

        this(parameterName, label, isRequired, false, null);
    }

    public AttributeMetaData(String parameterName, String label, AttributeType attributeType) {

        this(parameterName, label, false, false, attributeType);
    }

    /**
     * Constructor.
     *
     * @param parameterName The HTML parameter name of the attribute
     * @param label The label text to be used on a form
     * @param isRequireed If the attribute is required or not. Could be indicated on
     *                    the form and enforced with a validation listener
     * @param attributeType The data type of the attribute. String will be used as default in
     *                      other constructors
     */
    public AttributeMetaData(String parameterName,
                             String label,
                             boolean isRequired,
                             boolean isMultiple,
                             AttributeType attributeType) {

        m_parameterName = parameterName;
        m_label = label;
        m_isRequired = isRequired;
        m_isMultiple = isMultiple;
        m_attributeType = attributeType;
    }

    //*** Attribute methods

    public String getParameterName() {
        return m_parameterName;
    }

    public void setLabel(String label) {
        m_label = label;
    }

    public String getLabel() {
        return m_label;
    }

    public void isRequired(boolean isRequired) {
        m_isRequired = isRequired;
    }

    public boolean isRequired() {
        return m_isRequired;
    }

    public void isMultiple(boolean isMultiple) {
        m_isMultiple = isMultiple;
    }

    public boolean isMultiple() {
        return m_isMultiple;
    }

    public void setAttributeType(AttributeType attributeType) {
        m_attributeType = attributeType;
    }

    /**
     * Will return null if no attribute type has been set
     */
    public AttributeType getAttributeType() {
        return m_attributeType;
    }

    /**
     * This method may return null.
     */
    public ParameterModel getParameterModel() {

        ParameterModel parameterModel = null;

        if (m_attributeType != null) {
            Class parameterClass = m_attributeType.getParameterModelClass();

            if (parameterClass != null) {
                try {
                    parameterModel =
                        (ParameterModel)FormBuilderUtil.instantiateObjectOneArg(parameterClass.getName(), m_parameterName);
                } catch (Throwable t) {
                    // Let the parameter model be null
                }
            }
        }

        return parameterModel;
    }
}
