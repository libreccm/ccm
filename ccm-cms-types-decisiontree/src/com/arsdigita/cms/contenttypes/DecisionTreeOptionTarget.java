/*
 * Copyright (C) 2007 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.CustomCopy;
import com.arsdigita.cms.ItemCopier;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.Property;

import java.math.BigDecimal;


/**
 * A section target of the Camden Decision Tree content type.
 *
 * @author Carsten Clasohm
 * @version $Id$
 */
public class DecisionTreeOptionTarget extends ContentItem {
    
    /** PDL stuff                                                             */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.contenttypes.DecisionTreeOptionTarget";

    public static final String MATCH_OPTION     = "matchOption";
    public static final String TARGET_URL       = "targetURL";
    public static final String TARGET_SECTION   = "targetSection";

    /**
     * Default constructor, just delegates to a parameter constructor.
     */
    public DecisionTreeOptionTarget() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor.
     * 
     * @param oid
     * @throws DataObjectNotFoundException 
     */
    public DecisionTreeOptionTarget(OID oid) 
           throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor
     * @param id
     * @throws DataObjectNotFoundException 
     */
    public DecisionTreeOptionTarget(BigDecimal id) 
           throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Constructor
     * @param obj 
     */
    public DecisionTreeOptionTarget(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor
     * @param type 
     */
    public DecisionTreeOptionTarget(String type) {
        super(type);
    }

    /**
     * @return the base PDL object type for this item. Child classes
     * should override this method to return the correct value.
     */
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }
    
    /**
     * 
     * @return 
     */
    public DecisionTreeSectionOption getMatchOption() {
    	DataObject dataObject = (DataObject) get(MATCH_OPTION);
        if (dataObject == null) { return null; }
        return new DecisionTreeSectionOption(dataObject);
    }
    
    /**
     * 
     * @param value 
     */
    public void setMatchOption(DecisionTreeSectionOption value) {
    	setAssociation(MATCH_OPTION, value);
    }
    
    /**
     * 
     * @return 
     */
    public String getTargetURL() {
    	return (String) get(TARGET_URL); 
    }
    
    /**
     * 
     * @param value 
     */
    public void setTargetURL(String value) {
    	set(TARGET_URL, value);
    }
    
    /**
     * 
     * @return 
     */
    public DecisionTreeSection getTargetSection() {
    	DataObject dataObject = (DataObject) get(TARGET_SECTION);
        if (dataObject == null) { return null; }
        return new DecisionTreeSection(dataObject);
    }

    /**
     * 
     * @param value 
     */
    public void setTargetSection(DecisionTreeSection value) {
    	set(TARGET_SECTION, value);
    }

    /**
     * 
     * @param source
     * @param property
     * @param copier
     * @return 
     */
    @Override
    public boolean copyProperty(CustomCopy source, Property property,
                                ItemCopier copier) {
        String attribute = property.getName();
        if (TARGET_SECTION.equals(attribute)) {
            // We don't copy the TARGET_SECTION property here, because it's not marked
            // as a component, and would cause a PublishedLink to be created, which
            // results in performance problems during publication. Instead, we will 
            // set the TARGET_SECTION property as part of the copying of the SECTIONS 
            // property in DecisionTree.copyProperty().
            return true;
        }

        return super.copyProperty(source, property, copier);
    }
    
}