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

import com.arsdigita.cms.AuthoringKit;
import com.arsdigita.cms.AuthoringStep;
import com.arsdigita.cms.AuthoringStepCollection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ui.authoring.PageCreateDynamic;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.formbuilder.PersistentForm;
//import com.arsdigita.initializer.InitializationException;
import com.arsdigita.metadata.DynamicObjectType;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.Model;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;
import org.apache.oro.text.perl.Perl5Util;

import java.math.BigDecimal;

/** 
 * 
 * 
 */
public class UDCTHelper extends ContentTypeHelperImpl implements ContentTypeHelper {
    private static final Logger s_log 
        = Logger.getLogger(UDCTHelper.class);

    private static final String CATEGORIZATION_COMPONENT = 
      "com.arsdigita.cms.ui.authoring.ItemCategoryStep";
    private static final String CREATION_COMPONENT 
        = PageCreateDynamic.class.getName();

    private String m_parent;
    private ObjectType m_parentType;
    private ContentType m_type;
    private String m_name;

    private int m_stepCount = 1;
    // Flag to avoid the duplication of categorization step
    private boolean m_hasCategoryStep = false;

    /**
     * Constructor
     */
    public UDCTHelper() {
        // this is predefined
        setCreateComponent(CREATION_COMPONENT);
    }

    public void setName(String name) {
        m_name = name;
    }

    public String getName() {
        return m_name;
    }


    public void setParentType(String parentType) {
        m_parent = parentType;
        m_parentType =
            SessionManager.getMetadataRoot().getObjectType(parentType);
    }
    
    public ObjectType getParentType() {
        return m_parentType;
    }

    public ContentType getParentContentType() throws UncheckedWrapperException {
        ContentType parent;
        try {
            parent =  ContentType.findByAssociatedObjectType(m_parent); 
            return parent;
        } catch (DataObjectNotFoundException e) {
            throw new UncheckedWrapperException("Parent Type not found" , e);
        }
    }

    /**
     * 
     * @return 
     */
    @Override
    public AuthoringKit createAuthoringKit() {
        setCreateComponent(CREATION_COMPONENT);
        AuthoringKit kit = super.createAuthoringKit();
        addParentSteps();
        kit.save();
        return kit;
    }

    /** 
     * 
     * @return 
     */
    private DynamicObjectType createDOT() {
        DynamicObjectType dot = new DynamicObjectType(m_name, m_parentType);            
        dot.save();
        return dot;
    }

    /**
     * 
     * @return 
     */
    @Override
    public ContentType createType() {

        MetadataRoot root = SessionManager.getMetadataRoot();
        Model m= root.getModel(m_name);
        Perl5Util re = new Perl5Util();

        // XXX really wish I didn't have to do this and ObjectType had a getModel method
        int lastPeriod = m_parent.lastIndexOf(".");
        String modelName = m_parent.substring(0, lastPeriod);
        s_log.debug("ModelName is : " + modelName);
        m = root.getModel(modelName);
        
        ObjectType obj;
        if (m != null ) {
            s_log.debug("Model is : " + m.getName());
            obj = m.getObjectType(m_name);
            if ( obj == null ) {
                obj = createDOT().getObjectType();
            }
        } else {
            s_log.debug("No model");
            obj = createDOT().getObjectType();
        }
        s_log.debug("Dynamic object: " + obj.getQualifiedName());
        setObjectType(obj.getQualifiedName());

        m_type = super.createType();
        m_type.setID();

        // Add the persistent form components
        PersistentForm pForm = PersistentForm.create(m_name + "ItemForm");
        pForm.save();
        m_type.setItemFormID(pForm.getID());
        m_type.save();
        return m_type;
    }

    public void addParentSteps() {
        AuthoringStepCollection parentSteps 
            = getParentContentType().getAuthoringKit().getSteps();
        // add the steps from the parent type
        AuthoringKit kit = getAuthoringKit();
        s_log.debug("loading parent authoring steps");
        while (parentSteps.next()) {
            AuthoringStep step = parentSteps.getAuthoringStep();
            if (CATEGORIZATION_COMPONENT.equals(step.getComponent())) {
                m_hasCategoryStep = true;
            }
                
            kit.createStep(step.getLabel(), 
                           step.getDescription(), 
                           step.getComponent(),
                           new BigDecimal(m_stepCount));
            m_stepCount++;
        }
        kit.save();
        m_type.save();
    }

    /** 
     * 
     * @param label
     * @param description
     * @param component
     * @param ordering 
     */
    @Override
    public void addAuthoringStep(String label, 
                                 String description,
                                 String component,
                                 BigDecimal ordering) {
        // add the new steps after the parent
        ordering.add(new BigDecimal((new Integer(m_stepCount)).toString()));

        // potentially do something to avoid categorizing twice?
        super.addAuthoringStep(label, 
                               description,
                               component,
                               ordering);
    }
}
