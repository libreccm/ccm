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

package com.arsdigita.london.navigation.portlet;

import com.arsdigita.portal.Portlet;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;

import com.arsdigita.london.navigation.AbstractNavigationModel;
import com.arsdigita.london.navigation.DataCollectionDefinition;
import com.arsdigita.london.navigation.DataCollectionProperty;
import com.arsdigita.london.navigation.DataCollectionPropertyRenderer;
import com.arsdigita.london.navigation.ui.portlet.ObjectListPortletRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * Portlet to display a list of objects.
 * 
 * The objects can be selected / restricted by object type and filtered by a
 * category or category tree. Ordering and length of the list can be configured.
 * 
 * @version $Id: ObjectListPortlet.java 2070 2010-01-28 08:47:41Z pboy $
 */
public class ObjectListPortlet extends Portlet {
    private static final Logger s_log =
        Logger.getLogger( ObjectListPortlet.class );

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.london.navigation.portlet.ObjectListPortlet";

    public static final String BASE_OBJECT_TYPE = "baseObjectType";
    public static final String SPECIFIC_OBJECT_TYPE = "specificObjectType";
    public static final String ORDERING = "ordering";
    public static final String ATTRIBUTES = "xmlAttributes";
    public static final String PROPERTIES = "dcProperties";
    public static final String COUNT = "count";

    public static final String FILTER_CATEGORY = "filterCategory";

    public static final String CHECK_PERMISSIONS = "checkPermissions";
    public static final String DESCEND_CATEGORIES = "descendCategories";
    public static final String EXCLUDE_INDEX_OBJECTS = "excludeIndexObjects";

    private static final Map s_properties =
        Collections.synchronizedMap( new HashMap() );
    
    public ObjectListPortlet(DataObject dobj) {
        super(dobj);
    }

    public static void addProperty( String key, String title,
                                    DataCollectionProperty property,
                                    DataCollectionPropertyRenderer renderer ) {
        s_properties.put( key, new Object[] { title, property, renderer } );
    }

    public static Iterator getRegisteredProperties() {
        return s_properties.entrySet().iterator();
    }

    public static DataCollectionProperty getDCProperty( String key ) {
        Object[] entry = (Object[]) s_properties.get( key );
        if( null == entry ) {
            s_log.error( "dcProperty request for non-existent key: " + key );
            return null;
        }

        return (DataCollectionProperty) entry[1];
    }

    public static DataCollectionPropertyRenderer getDCPropertyRenderer( String key ) {
        Object[] entry = (Object[]) s_properties.get( key );
        if( null == entry ) {
            s_log.error( "dcPropertyRenderer request for non-existent key: " + key );
            return null;
        }

        return (DataCollectionPropertyRenderer) entry[2];
    }
    
    public void initialize() {
        super.initialize();
        
        if (isNew()) {
            set(CHECK_PERMISSIONS, Boolean.FALSE);
            set(DESCEND_CATEGORIES, Boolean.FALSE);
            set(EXCLUDE_INDEX_OBJECTS, Boolean.TRUE);
        }
    }
    
    public void setBaseObjectType(String objectType) {
        set(BASE_OBJECT_TYPE, objectType);
    }

    public String getBaseObjectType() {
        return (String)get(BASE_OBJECT_TYPE);
    }
    
    public void setRestrictedObjectType(String specificObjectType) {
        set(SPECIFIC_OBJECT_TYPE, specificObjectType);
    }

    public String getRestrictedObjectType() {
        return (String)get(SPECIFIC_OBJECT_TYPE);
    }

    public void setOrdering(String ordering) {
        set(ORDERING, ordering);
    }

    public String getOrdering() {
        return (String)get(ORDERING);
    }
    
    public void setAttributes(String attributes) {
        set(ATTRIBUTES, attributes);
    }

    public String getAttributes() {
        return (String)get(ATTRIBUTES);
    }
    
    public void setCount(int count) {
        set(COUNT, new Integer(count));
    }
    
    public int getCount() {
        return ((Integer)get(COUNT)).intValue();
    }
    
    public void setCheckPermissions(boolean check) {
        set(CHECK_PERMISSIONS, new Boolean(check));
    }
    
    public boolean isCheckingPermissions() {
        return ((Boolean)get(CHECK_PERMISSIONS)).booleanValue();
    }

    public void setFilterCategory(Category cat) {
        setAssociation(FILTER_CATEGORY, cat);
    }
    
    public Category getFilterCategory() {
        return (Category)DomainObjectFactory
            .newInstance((DataObject)get(FILTER_CATEGORY));
    }

    public void setDescendCategories(boolean descend) {
        set(DESCEND_CATEGORIES, new Boolean(descend));
    }
    
    public boolean isDescendingCategories() {
        return ((Boolean)get(DESCEND_CATEGORIES)).booleanValue();
    }

    public void setExcludeIndexObjects(boolean exclude) {
        set(EXCLUDE_INDEX_OBJECTS, new Boolean(exclude));
    }
    
    public boolean isExludingIndexObjects() {
        return ((Boolean)get(EXCLUDE_INDEX_OBJECTS)).booleanValue();
    }

    public void setProperties( Object[] properties ) {
        StringBuffer buf = new StringBuffer();

        if( null != properties ) {
            for( int i = 0; i < properties.length; i++ ) {
                buf.append( properties[i].toString() );

                if( i != properties.length - 1 ) {
                    buf.append( ',' );
                }
            }
        }

        set( PROPERTIES, buf.toString() );
    }

    public String[] getProperties() {
        String properties = (String) get( PROPERTIES );
        if( null == properties ) return new String[] { };

        StringTokenizer tok = new StringTokenizer( properties, "," );
        String[] result = new String[ tok.countTokens() ];

        for( int i = 0; i < result.length; i++ ) {
            result[i] = tok.nextToken();
        }

        return result;
    }
    
    protected DataCollectionDefinition newDataCollectionDefinition() {
        return new DataCollectionDefinition();
    }

    public DataCollectionDefinition getDataCollectionDefinition() {
        DataCollectionDefinition def = newDataCollectionDefinition();
        
        def.setObjectType(getBaseObjectType());
        def.setSpecificObjectType(getRestrictedObjectType());

        def.addOrder(getOrdering());

        def.setFilterCategory(getFilterCategory() != null);
        def.setDescendCategories(isDescendingCategories());
        def.setCheckPermissions(isCheckingPermissions());
        def.setExcludeIndexObjects(isExludingIndexObjects());

        String[] properties = getProperties();
        for( int i = 0; i < properties.length; i++ ) {
            DataCollectionProperty dcp = getDCProperty( properties[i] );
            def.addProperty( dcp );
        }

        return def;
    }
    
    public DataCollection getDataCollection() {
        DataCollectionDefinition def = getDataCollectionDefinition();
        def.lock();        
        return def.getDataCollection(new ObjectListPortletModel(this));
    }


    protected AbstractPortletRenderer doGetPortletRenderer() {
        return new ObjectListPortletRenderer(this);
    }
    
    private class ObjectListPortletModel extends AbstractNavigationModel {

        private ObjectListPortlet m_portlet;

        public ObjectListPortletModel(ObjectListPortlet portlet) {
            m_portlet = portlet;
        }

        protected ACSObject loadObject() {
            return null;
        }
        
        protected Category loadCategory() {
            return m_portlet.getFilterCategory();
        }
        
        protected Category[] loadCategoryPath() {
            Category cat = getCategory();
            if (cat == null) {
                return new Category[0];
            }

            CategoryCollection parents = cat.getDefaultAscendants();
            parents.addOrder("defaultAncestors");
            List path = new ArrayList();
            while (parents.next()) {
                path.add(parents.getDomainObject());
            }
            return (Category[])path.toArray(new Category[path.size()]);
        }
        
        protected Category loadRootCategory() {
            Category[] path = getCategoryPath();
            return path.length == 0 ? null : path[0];
        }
    }
}
