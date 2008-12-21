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
 */

package com.arsdigita.london.navigation;

import com.arsdigita.bebop.PageState;
import com.arsdigita.categorization.Category;


import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;

import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.SessionManager;

import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;

import com.arsdigita.util.Assert;
import com.arsdigita.util.LockableImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * A class for building up a definition of parameters to
 * later use for a DataCollection of ACSObjects.
 */
public class DataCollectionDefinition extends LockableImpl {
    
    private String m_objectType = ACSObject.BASE_DATA_OBJECT_TYPE;
    private String m_specificObjectType = null;
    
    private boolean m_filterCategory = true;
    private boolean m_descendCategories = false;
    private boolean m_excludeIndexObjects = true;
    private boolean m_checkPermissions = false;
    private boolean m_blackListTypes = false;
    
    private ArrayList m_ordering = new ArrayList();
    private ArrayList m_excludedTypes = new ArrayList();
    private ArrayList m_properties = new ArrayList();
    
    private String m_dateAttribute = null;

    public final void setObjectType(String objectType) {
        Assert.unlocked(this);
        validateObjectType(objectType);
        m_objectType = objectType;
    }

    public final void setSpecificObjectType(String specificObjectType) {
        Assert.unlocked(this);

        if (specificObjectType != null) { 
            validateObjectType(specificObjectType);
        }
        m_specificObjectType = specificObjectType;
    }

    public final void excludeSpecificObjectType( String specificObjectType ) {
        Assert.unlocked( this );

        m_excludedTypes.add( specificObjectType );
    }

    private static Logger s_log = Logger.getLogger(DataCollectionDefinition.class);

    public void setDateAttribute (DataCollectionRenderer renderer) {
        ObjectType type = SessionManager.getMetadataRoot().getObjectType(m_objectType);
        s_log.debug("set date attribute for collection of " + type.getQualifiedName());
        if (s_log.isDebugEnabled()) {
            Iterator properties = type.getProperties();
            while (properties.hasNext()) {
                Property prop = (Property)properties.next();
                s_log.debug("object has property " + prop.getName() + " of class " + prop.getJavaClass().getName());
            }
        }
        Iterator it = (renderer.getAttributes().iterator());
        while (it.hasNext()) {
            String attribute = (String)it.next();
            s_log.debug("renderer is rendering attribute: " + attribute);
            Property property = type.getProperty(attribute);
            s_log.debug("property of object: " + property);
            if (property != null) {
                s_log.debug("Property class is " + property.getJavaClass().getName());
            }
            if (property != null && property.getJavaClass().getName().equals(Date.class.getName())) {
                m_dateAttribute = attribute;
                // if more than one date attribute is specified for the type included in this
                // definition, then we cannot determine in the code which is to be used as the
                // basis for ordering in XSL - therefore return the first encountered date.
                // User can control which of the dates is used as basis for sorting by specifying
                // that date attribute first in the navigation jsp
                break;
            }
        }
    }

    private final void validateObjectType(String objectType) {
        ObjectType type = SessionManager.getMetadataRoot().getObjectType(objectType);
        Assert.exists(type, ObjectType.class);
        validateObjectType(type);
    }
    
    protected void validateObjectType(ObjectType type) {
        // Nada
    }
    
    public final void setFilterCategory(boolean filterCategory) {
        Assert.unlocked(this);
        m_filterCategory = filterCategory;
    }
    
    public final void setDescendCategories(boolean descendCategories) {
        Assert.unlocked(this);
        m_descendCategories = descendCategories;
    }
    
    public final void setExcludeIndexObjects(boolean excludeIndexObjects) {
        Assert.unlocked(this);
        m_excludeIndexObjects = excludeIndexObjects;
    }
    
    public final void setCheckPermissions(boolean checkPermissions) {
        Assert.unlocked(this);
        m_checkPermissions = checkPermissions;
    }

    /**
     * Activates a filter for content types which are blacklisted 
     * in the AtoZ module.
     */
    public void setBlackListTypes(boolean blackListTypes) {
    	Assert.unlocked(this);
    	m_blackListTypes = blackListTypes;
    }
    
    public final void addOrder(String order) {
        Assert.unlocked(this);
        m_ordering.add(order);
    }

    public final void addProperty( DataCollectionProperty property ) {
        Assert.unlocked( this );
        m_properties.add( property );
    }
    
    public final DataCollection getDataCollection(NavigationModel model) {
        Assert.locked(this);

        DataCollection objects = SessionManager.getSession()
            .retrieve(m_objectType);
        
        applyFilters(objects, model);

        Iterator properties = m_properties.iterator();
        while( properties.hasNext() ) {
            DataCollectionProperty property = (DataCollectionProperty)
                properties.next();
            property.addProperty( objects );
        }

        if (model.getCategory() != null) {
            // for date ordered categories, if pagination occurs, we need to ensure
            // that primary ordering is the date attribute included in the renderer
            // if there is one
            if (s_log.isDebugEnabled()) {
                s_log.debug("Category is " + model.getCategory().getID() + ": " + model.getCategory().getName());
                s_log.debug("getting data collection. Is category date ordered? "  
                    + Navigation.getConfig().isDateOrderedCategory(model.getCategory(), PageState.getPageState()) 
                    + " date attribute has been set to " + m_dateAttribute);
            }
            if (Navigation.getConfig().isDateOrderedCategory(model.getCategory(), PageState.getPageState()) && m_dateAttribute != null) {
                objects.addOrder(m_dateAttribute + " desc");
            }
        }

        if (m_ordering.size() > 0) {
            Iterator orders = m_ordering.iterator();
            while (orders.hasNext()) {
                objects.addOrder((String)orders.next());
            }
        } else {
            objects.addOrder("id desc");
        }

        return objects;
    }
    
    /** Can be overridden to extract differently the category to inspect. */
    protected Category getCategory(NavigationModel model) {
        return model.getCategory();
    }

    protected void applyFilters(DataCollection objects, 
                                NavigationModel model) {
        if (m_specificObjectType != null) {
            objects.addEqualsFilter(ACSObject.OBJECT_TYPE, m_specificObjectType);
        }

        Iterator excludedTypes = m_excludedTypes.iterator();
        while (excludedTypes.hasNext()) {
            String excludedType = excludedTypes.next().toString();
            objects.addFilter(ACSObject.OBJECT_TYPE + " != '" + 
                              excludedType + "'" );
        }
        
        if (m_blackListTypes) {
        	objects.addNotInSubqueryFilter(ACSObject.OBJECT_TYPE, 
        			"com.arsdigita.london.navigation.blackListTypes");
        }

        Category cat = getCategory(model);
        if (m_filterCategory && cat != null) {
            if (m_descendCategories) {
                Filter children = objects.addInSubqueryFilter( 
                    getCategorizedObjectPath("id"),
                    "com.arsdigita.categorization.objectIDsInSubtree"); 
                children.set( "categoryID", cat.getID() );
                if (m_excludeIndexObjects) {
                    Filter moreChildren = objects.addNotInSubqueryFilter( 
                        getCategorizedObjectPath("id"),
                        "com.arsdigita.categorization.liveIndexItemsInSubtree"
                    );
                    moreChildren.set( "categoryID", cat.getID() );
                }
            } else {
                objects.addEqualsFilter(
                    getCategorizedObjectPath("categories.id"), 
                    cat.getID());
                if (m_excludeIndexObjects) {
                    Filter moreChildren = objects.addNotInSubqueryFilter( 
                        getCategorizedObjectPath("id"),
                        "com.arsdigita.categorization.liveIndexItemsInCategory"
                    );
                    moreChildren.set( "categoryID", cat.getID() );
                }
            }
        } 
        
        if (m_checkPermissions) {
            // allow subclasses to override the permission check
            checkPermissions(objects);
        }
     }

    protected void checkPermissions(DataCollection objects) {
        Party party = Kernel.getContext().getParty();
        if (party == null) {
            party = Kernel.getPublicUser();
        }

        PermissionService.filterObjects(
                  objects,
                  PrivilegeDescriptor.READ,
                  party.getOID());
    }
    
    protected String getCategorizedObjectPath(String fragment) {
        return fragment;
    }
}
