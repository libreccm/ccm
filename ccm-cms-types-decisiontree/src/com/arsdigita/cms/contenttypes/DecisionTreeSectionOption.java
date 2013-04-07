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

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

/**
 * A section option of the Camden Decision Tree content type.
 *
 * @author Carsten Clasohm
 * @version $Id$
 */
public class DecisionTreeSectionOption extends ContentItem {
	private static final Logger s_log = Logger.getLogger(
                                            DecisionTreeSectionOption.class);
    
    public static final String BASE_DATA_OBJECT_TYPE =
                        "com.arsdigita.cms.contenttypes.DecisionTreeSectionOption";

    public static final String TREE_SECTION = "treeSection";
    public static final String RANK         = "rank";
    public static final String LABEL        = "label";
    public static final String VALUE        = "value";

    public DecisionTreeSectionOption() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public DecisionTreeSectionOption(OID oid) 
           throws DataObjectNotFoundException {
        super(oid);
    }

    public DecisionTreeSectionOption(BigDecimal id) 
           throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public DecisionTreeSectionOption(DataObject obj) {
        super(obj);
    }

    public DecisionTreeSectionOption(String type) {
        super(type);
    }

    /**
     * @return the base PDL object type for this item. Child classes
     * should override this method to return the correct value.
     */
    @Override
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }
    
    public DecisionTreeSection getSection() {
    	DataObject dataObject = (DataObject) get(TREE_SECTION);
        if (dataObject == null) { return null; }
        return new DecisionTreeSection(dataObject);
    }

    public void setSection(DecisionTreeSection value) {
    	setAssociation(TREE_SECTION, value);
    }
    
    public Integer getRank() {
    	return (Integer) get(RANK);
    }
    
    public void setRank(Integer value) {
    	set(RANK, value);
    }
    
    public String getLabel() {
    	return (String) get(LABEL); 
    }
    
    public void setLabel(String value) {
    	set(LABEL, value);
    }
    
    public String getValue() {
    	return (String) get(VALUE); 
    }
    
    public void setValue(String value) {
    	set(VALUE, value);
    }

}