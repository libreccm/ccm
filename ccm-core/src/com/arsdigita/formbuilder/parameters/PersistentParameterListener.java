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
package com.arsdigita.formbuilder.parameters;


// The DomainObject class that we are extending
import com.arsdigita.kernel.ACSObject;

// For the id of the persistent parameter listener
import java.math.BigDecimal;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;

// Thrown if the underlying DataObject with given id cannot be found
import com.arsdigita.domain.DataObjectNotFoundException;

// The interface of the classes we are persisting
import com.arsdigita.bebop.event.ParameterListener;

// To instantiate listener of right class
import com.arsdigita.formbuilder.util.FormBuilderUtil;

// We work with these classes to store attributes of listeners
import com.arsdigita.bebop.util.Attributes;
import com.arsdigita.formbuilder.util.AttributeHelper;

// ACS 5 uses Log4J for logging
import org.apache.log4j.Logger;

import com.arsdigita.util.Assert;


/**
 * The base class of all persistent parameter listeners.
 *
 * @author Peter Marklund
 * @version $Id: PersistentParameterListener.java 287 2005-02-22 00:29:02Z sskracic $
 *
 */
public class PersistentParameterListener extends ACSObject {

    private static final Logger s_log =
        Logger.getLogger(PersistentParameterListener.class.getName());

    // Attributes are stored in memory in this object before they
    // are saved in the attributeString attribute
    private Attributes m_attributes;
    private boolean m_attributeChanged = false;

    /**
     * The fully qualified name of the underlying DataObject of this class.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.Listener";

    // *** Constructors -------------

    /**
     * Constructor for creating a new listener that can be persisted
     */
    public PersistentParameterListener() {
        super(BASE_DATA_OBJECT_TYPE);
        set("defaultDomainClass", this.getClass().getName());
    }


    /**
     * Constructor for creating a new listener that can be persisted
     */
    public PersistentParameterListener(String className) {

        super(BASE_DATA_OBJECT_TYPE);

        set("className", className);

        set("defaultDomainClass", this.getClass().getName());
    }


    /**
     * Constructor for retrieving a persistent parameter listener
     * from the database
     */
    public PersistentParameterListener(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Internal constructor to be used by subclasses
     */
    protected PersistentParameterListener(OID oID)
        throws DataObjectNotFoundException {

        super(oID);
    }

    public PersistentParameterListener(DataObject obj) {
        super(obj);
    }

    protected void beforeSave() {
        if (m_attributeChanged) {
            Assert.exists(m_attributes, "Attribute map");
            set("attributeString",
                AttributeHelper.getAttributeString(m_attributes));
            m_attributeChanged = false;
        }

        super.beforeSave();
    }

    /**
     * Factory method that creates the Bebop component whose
     * persistence is handled by this domain object.
     */
    public ParameterListener createListener() {

        return (ParameterListener)FormBuilderUtil.instantiateObject(getClassName());
    }

    // *** Attribute Methods

    public void setClassName(String className) {
        set("className", className);
    }

    public String getClassName() {
        return (String)get("className");
    }

    /**
     * For use internally by extending classes to set Listener attributes
     */
    protected void setListenerAttribute(String name, String value) {
        if (m_attributes == null)
            loadAttributes();

        m_attributes.setAttribute(name, value);
        m_attributeChanged = true;
        if (m_attributeChanged == false) {
            m_attributeChanged = true;
            // force persistence to run beforeSave eventually
            set("attributeString", get("attributeString"));
        }
    }

    /**
     * For use internally by extending classes to get Listener attributes
     */
    protected String getListenerAttribute(String name) {
        if (m_attributes == null)
            loadAttributes();

        return m_attributes.getAttribute(name);
    }

    private void loadAttributes() {
        String attrs = (String)get("attributeString");
        if (attrs == null) {
            m_attributes = new Attributes();
        } else {
            m_attributes = (new AttributeHelper()).getAttributesMap((String)get("attributeString"));
        }
        m_attributeChanged = false;
    }
}
