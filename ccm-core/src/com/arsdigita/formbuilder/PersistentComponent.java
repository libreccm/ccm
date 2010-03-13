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


// The DomainObject class that we are extending
import com.arsdigita.auditing.AuditedACSObject;

// Thrown if the underlying DataObject with given id cannot be found
import com.arsdigita.domain.DataObjectNotFoundException;

// The PersistentComponent can create a Bebop Component
import com.arsdigita.bebop.Component;

import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;

// ACS objects are identified by a number
import com.arsdigita.util.Assert;

// We work with these classes when return an HTML attribute
import com.arsdigita.bebop.util.Attributes;
import com.arsdigita.formbuilder.util.AttributeHelper;

// ACS 5 uses Log4J for logging
import org.apache.log4j.Logger;


/**
 * The super class of all domain objects in the Form Builder service
 * that enable persistence of Bebop Forms and their Components. Every
 * sub class of this class will manage persistence of a particular Bebop
 * component. Of
 * special importance to the application developer is the save() method
 * which is used to persist a component. The getID() method returns an
 * ACSObject to be used when retrieving the component with the
 * createComponent(BigDecimal) method.
 *
 * @see com.arsdigita.formbuilder.SimpleQuestionnaire
 *
 * @author Peter Marklund
 * @version $Id: PersistentComponent.java 317 2005-03-11 19:04:37Z mbooth $
 *
 */
public abstract class PersistentComponent extends AuditedACSObject {

    public static final String versionId = "$Id: PersistentComponent.java 317 2005-03-11 19:04:37Z mbooth $ by $Author: mbooth $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(PersistentComponent.class.getName());

    /**
     * The fully qualified name of the underlying DataObject of this class.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.Component";

    public static final String ATTRIBUTE_STRING = "attributeString";

    // Attributes are stored in memory in this object before they
    // are saved in the attributeString attribute
    private Attributes m_attributes;
    private boolean m_attributeChanged = false;

    // *** Constructors -------------

    private RuntimeException m_creation;

    /**
     * Sub classes can create a new component domain object with
     * this constructor.
     */
    public PersistentComponent(String objectType) {
        super(objectType);

        // We need to save the class name of this factory object be able
        // to resurrect it
        set("defaultDomainClass", this.getClass().getName());

        m_creation = new RuntimeException( getClass().getName() );
    }

    public PersistentComponent(ObjectType type) {
        super(type);

        m_creation = new RuntimeException( getClass().getName() );
    }

    /**
     * Sub classes may use this constructor to retrieve an existing
     * Component domain object with an id and object type
     */
    public PersistentComponent(OID oID)
        throws DataObjectNotFoundException {

        super(oID);

        m_creation = new RuntimeException( getClass().getName() );
    }


    public PersistentComponent(DataObject obj) {
        super(obj);


        m_creation = new RuntimeException( getClass().getName() );
    }

    /**
     * Save this PersistentComponent. All component
     * attributes and the class name of this PersistentComponent
     * instance are saved.
     */
    protected void beforeSave() {
        if (m_attributeChanged) {
            Assert.exists(m_attributes, "Attribute map");
            set(ATTRIBUTE_STRING,
                AttributeHelper.getAttributeString(m_attributes));
            m_attributeChanged = false;
        }

        if( s_log.isDebugEnabled() ) {
            s_log.debug( "Saving a " + getClass().getName() );
        }

        try {
        super.beforeSave();
        } catch( Exception ex ) {
            throw m_creation;
        }
    }

    /**
     * Factory method that creates the Bebop component whose
     * persistence is handled by this domain object.
     */
    public abstract Component createComponent();

    // *** Attribute Methods

    /**
     * The SimpleQuestionnaire needs to set this so that we
     * can distinguish SimpleQuestionnaires from other PersistentForms
     */
    protected void setDomainClass(String domainClass) {
        set("defaultDomainClass", domainClass);
    }

    /**
     * For use internally by extending classes to set Component attributes (
     * usually HTML attributes)
     */
    protected void setComponentAttribute(String name, String value) {
        if (m_attributes == null) loadAttributes();

        m_attributes.setAttribute(name, value);
        if (m_attributeChanged == false) {
            m_attributeChanged = true;
            // force persistence to run beforeSave eventually
            set(ATTRIBUTE_STRING, null);
        }
    }

    /**
     * For use internally by extending classes to get Component attributes (
     * usually HTML attributes)
     */
    protected String getComponentAttribute(String name) {
        if (m_attributes == null)
            loadAttributes();

        return m_attributes.getAttribute(name);
    }

    private void loadAttributes() {
        String attrs = (String)get(ATTRIBUTE_STRING);
        if (attrs == null) {
            m_attributes = new Attributes();
        } else {
            m_attributes = (new AttributeHelper()).getAttributesMap((String)get(ATTRIBUTE_STRING));
        }
        m_attributeChanged = false;
    }

    /* The following are attributes that normal Bebop
     * Forms don't have
     */

    public void setAdminName(String adminName) {
        set("adminName", adminName);
    }

    public String getAdminName() {
        return (String)get("adminName");
    }

    public void setDescription(String description) {
        set("description", description);
    }

    public String getDescription() {
        return (String)get("description");
    }

    //*** Attribute metadata
    public AttributeMetaDataList getAttributeMetaData() {

        AttributeMetaDataList list = new AttributeMetaDataList();

        // I currently don't want these attributes on the Form Builder admin UI
        //list.add(new AttributeMetaData("adminName", "Admin name"));
        //list.add(new AttributeMetaData("description", "Description"));

        return list;
    }

    // Metadata for authoring

    /**
     * Is this object editable after creation? eg A form section isn't, you must
     * edit the target item.
     */
    public boolean isEditable() {
        return true;
    }

    /**
     * Does this widget have an associated label? eg A horizontal rule doesn't.
     */
    public boolean hasLabel() {
        return true;
    }
}
