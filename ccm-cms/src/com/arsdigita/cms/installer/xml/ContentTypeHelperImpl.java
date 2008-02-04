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
package com.arsdigita.cms.installer.xml;

import com.arsdigita.cms.AuthoringKit;
import com.arsdigita.cms.AuthoringStep;
import com.arsdigita.cms.AuthoringStepCollection;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.search.ContentPageMetadataProvider;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.search.MetadataProviderRegistry;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

public class ContentTypeHelperImpl implements ContentTypeHelper {
    private static final Logger s_log 
        = Logger.getLogger(ContentTypeHelperImpl.class);
    
    private ContentType m_type;
    private ContentType m_parent;

    // Basic Content type properties
    private String m_label;
    private String m_labelKey;
    private String m_labelBundle;
    private String m_description;
    private String m_descriptionKey;
    private String m_descriptionBundle;
    private String m_objectType;
    private String m_className;
    private String m_createComponent;
    private AuthoringKit m_kit;

    private boolean m_internal;

    public ContentTypeHelperImpl() {}

    /**
     *  @deprecated use setLabelBundle and setLabelKey
     */
    public void setLabel(String label) {
        m_label = label;
    }
        
    /**
     *  @deprecated use getLabelBundle and getLabelKey
     */
    public String getLabel() {
        return m_label;
    }

    /**
     *  The labelBundle and labelKey work together to specify where
     *  to locate the given label.  These are the bundle and key use
     *  to create a GlobalizedMessage
     */
    public void setLabelBundle(String labelBundle) {
        m_labelBundle = labelBundle;
    }
        
    public String getLabelBundle() {
        return m_labelBundle;
    }

    /**
     *  The labelBundle and labelKey work together to specify where
     *  to locate the given label.  These are the bundle and key use
     *  to create a GlobalizedMessage
     */
    public void setLabelKey(String labelKey) {
        m_labelKey = labelKey;
    }
        
    public String getLabelKey() {
        return m_labelKey;
    }

    public void setInternal(boolean internal) {
        m_internal = internal;
    }

    public boolean isInternal() {
        return m_internal;
    }

    /**
     *  @deprecated use setLabelBundle and setLabelKey
     */
    public void setDescription(String description) {
        m_description = description;
    }

    /**
     *  @deprecated use getLabelBundle and getLabelKey
     */
    public String getDescription() {
        return m_description;
    }

    /**
     *  The descriptionBundle and descriptionKey work together to specify where
     *  to locate the given description.  These are the bundle and key use
     *  to create a GlobalizedMessage
     */
    public void setDescriptionBundle(String descriptionBundle) {
        m_descriptionBundle = descriptionBundle;
    }
        
    public String getDescriptionBundle() {
        return m_descriptionBundle;
    }

    /**
     *  The descriptionBundle and descriptionKey work together to specify where
     *  to locate the given description.  These are the bundle and key use
     *  to create a GlobalizedMessage
     */
    public void setDescriptionKey(String descriptionKey) {
        m_descriptionKey = descriptionKey;
    }
        
    public String getDescriptionKey() {
        return m_descriptionKey;
    }

    public void setObjectType(String objType) {
        m_objectType = objType;
    }

    public String getObjectType() {
        return m_objectType;
    }
        
    public void setClassName(String classname) {
        m_className = classname;
    }

    public String getClassName() {
        return m_className;
    }

    public void setCreateComponent(String createComponent) {
        m_createComponent = createComponent;
    }

    public AuthoringKit getAuthoringKit() {
        Assert.exists(m_kit);
        return m_kit;
    }

    public ContentType getContentType() {
        Assert.exists(m_type);
        return m_type;
    }
    

    /** Doesn't do anything */
    public void setName(String name) {}
    /** Doesn't do anything */
    public void setParentType(String parentType) {}


    public ContentType createType() {
        Assert.exists(m_label);
        Assert.exists(m_description);
        Assert.exists(m_objectType);
        Assert.exists(m_className);

        try { 
            s_log.debug("making new content type");
            m_type =
                ContentType.findByAssociatedObjectType(m_objectType); 
            m_type.setLabel(m_label);
            m_type.setDescription(m_description);
            m_type.save();
        } catch (DataObjectNotFoundException e) {
            s_log.debug("Looking for content type");
            s_log.debug("Creating ContentType Label: " + m_label 
                        + " Description: "  + m_description 
                        + " className: " + m_className 
                        + " AssociatedObjectType: " + m_objectType);
                
            // this is what would need to be changed to make the
            // label and description multi-lingual.  To do that,
            // you need to use labelKey, labelBundle, descriptionKey
            // and descriptionBundle
            m_type = new ContentType();
            m_type.setLabel(m_label);
            m_type.setDescription(m_description);
            m_type.setClassName(m_className);
            m_type.setAssociatedObjectType(m_objectType);
            m_type.setInternal(m_internal);
            m_type.save();
        } 

        // Turn on search indexing for this type
        ObjectType type = SessionManager.getMetadataRoot()
            .getObjectType(m_objectType);
        if (type.isSubtypeOf(ContentPage.BASE_DATA_OBJECT_TYPE) &&
            !m_internal) {
            s_log.debug("Registering search adapter for " +
                        m_objectType);
            MetadataProviderRegistry.registerAdapter(
                m_objectType,
                new ContentPageMetadataProvider());
        }
    
        Assert.exists(m_type);
        return m_type;
    }

    public AuthoringKit createAuthoringKit() {
        Assert.exists(m_type);

        s_log.debug("Createcomponent is : " + m_createComponent);


        m_kit = m_type.getAuthoringKit();
        if (m_kit != null) {
            // We remove all the existing AuthoringSteps
            if (m_createComponent != null) {
                m_kit.setCreateComponent(m_createComponent);
            }
            AuthoringStepCollection ac = m_kit.getSteps();
            while (ac.next()) {
                AuthoringStep step = ac.getAuthoringStep();
                s_log.debug("Deleting authoringStep " 
                            + step.getLabel());
                m_kit.removeStep(step);
                step.delete();
            }

        } else {
            // This updates the createComponent


            m_kit = m_type.createAuthoringKit(m_createComponent);
        }
        s_log.debug("saving authoring kit");
        m_kit.save();
        m_type.save();
        return m_kit;
    }

    /**
     *  @deprecated
     */
    public void addAuthoringStep(String label, 
                                 String description,
                                 String component,
                                 BigDecimal ordering) {
        addAuthoringStep(label, null, description, null, component, ordering);
    }

    public void addAuthoringStep(String labelKey, 
                                 String labelBundle,
                                 String descriptionKey,
                                 String descriptionBundle,
                                 String component,
                                 BigDecimal ordering) {
        s_log.debug("Creating AuthoringStep " 
                    +  " LabelKey: " + labelKey
                    +  " LabelBundle: "  + labelBundle 
                    +  " DescriptionKey: "  + descriptionKey
                    +  " DescriptionBundle: "  + descriptionBundle 
                    + " Component " + component
                    + " Ordering: " + ordering);
        
        Assert.exists(m_kit);
        Assert.exists(labelKey);
        Assert.exists(descriptionKey);
        Assert.exists(component);
        Assert.exists(ordering);

        m_kit.createStep(labelKey, labelBundle,
                         descriptionKey, descriptionBundle,
                         component, ordering);
        m_kit.save();
        m_type.save();
    }

    public void saveType() {
        m_kit.save();
        m_type.save();
    }

}
