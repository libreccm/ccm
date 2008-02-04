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

import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.util.Assert;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.util.StringUtils;
import com.arsdigita.xml.Element;

// Quasimodo: Begin
import com.arsdigita.web.URL;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.Web;
// Quasimodo: End

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class DataCollectionRenderer extends LockableImpl {

    private static final Logger s_log =
        Logger.getLogger(DataCollectionRenderer.class);

    private ArrayList m_attributes = new ArrayList();
    private ArrayList m_properties = new ArrayList();

    private int m_pageSize = 20;
    private boolean m_specializeObjects = false;
    private boolean m_wrapAttributes = false;
    private boolean m_navItems = true;

    public DataCollectionRenderer() {
        addAttribute("objectType");
        addAttribute("id");
    }

    public void addAttribute(String name) {
        Assert.unlocked(this);
        m_attributes.add(name);
    }

    public void addProperty(DataCollectionPropertyRenderer pr) {
        Assert.unlocked(this);
        m_properties.add(pr);
    }

    public void setPageSize(int pageSize) {
        Assert.unlocked(this);
        m_pageSize = pageSize;
    }

    /**
     * Specify whether to include the items for navigation that are within
     * same category.
     * This flag toggles the generation of nav:item xml elements.
     */
    public void setNavItems (boolean navItems){
        m_navItems = navItems;
    }

    public void setSpecializeObjects(boolean specializeObjects) {
        Assert.unlocked(this);
        m_specializeObjects = specializeObjects;
    }

    public void setWrapAttributes(boolean wrapAttributes) {
        Assert.unlocked(this);
        m_wrapAttributes = wrapAttributes;
    }

    /**
     * @pageNumber current page, starting from 1
     */
    public Element generateXML(DataCollection objects,
                               int pageNumber) {
        Assert.locked(this);

        // Quasimodo: Begin
        // If objects is empty, do not insert objectList-element but do insert noContent-element
        // and return immediately
        if(objects.isEmpty()) {
            return Navigation.newElement("noContent");
        }
        // Quasimodo: End
        
        Element content = Navigation.newElement("objectList");

        //Return the empty nav:item & nav:paginator tags.
        // Quasimodo: Why should I??? There is no need for a paginator if there aren't any elements
        if (! m_navItems) {
                Element paginator = Navigation.newElement("paginator");
                content.addContent(paginator);
                return content;
        }

        long objectCount = objects.size();
        int pageCount = (int)Math.ceil((double)objectCount / (double)m_pageSize);

        if (pageNumber < 1) {
            pageNumber = 1;
        }

        if (pageNumber > pageCount) {
            pageNumber = (pageCount == 0 ? 1 : pageCount);
        }

        long begin = ((pageNumber-1) * m_pageSize);
        int count = (int)Math.min(m_pageSize, (objectCount - begin));
        long end = begin + count;

        if (count != 0) {
            objects.setRange(new Integer((int)begin+1), new Integer((int)end+1));
        }

        Element paginator = Navigation.newElement("paginator");

        // Quasimodo: Begin
        // Copied from com.arsdigita.search.ui.ResultPane
        String pageParam = "pageNumber";
        
        URL url = Web.getContext().getRequestURL();
        ParameterMap map = new ParameterMap();

        if (url.getParameterMap() != null) {
            Iterator current = url.getParameterMap().keySet().iterator();
            while (current.hasNext()) {
                String key = (String)current.next();
                if (key.equals(pageParam)) {
                    continue;
                }
                map.setParameterValues(key, url.getParameterValues(key));
            }
        }
        
        paginator.addAttribute("pageParam", pageParam);
        paginator.addAttribute("baseURL", URL.there(url.getPathInfo(), map).toString());
        // Quasimodo: End
        
        paginator.addAttribute("pageNumber", new Long(pageNumber).toString());
        paginator.addAttribute("pageCount", new Long(pageCount).toString());
        paginator.addAttribute("pageSize", new Long(m_pageSize).toString());
        paginator.addAttribute("objectBegin", new Long(begin+1).toString());
        paginator.addAttribute("objectEnd", new Long(end).toString());
        paginator.addAttribute("objectCount", new Long(objectCount).toString());

        content.addContent(paginator);

        int index = 0;
        while (objects.next()) {
            DataObject dobj = objects.getDataObject();
            ACSObject object = null;
            if (m_specializeObjects) {
                object = (ACSObject)
                    DomainObjectFactory.newInstance(dobj);
            }

            Element item = Navigation.newElement("item");

            Iterator attributes = m_attributes.iterator();
            while (attributes.hasNext()) {
                String name = (String)attributes.next();
                String[] paths = StringUtils.split(name, '.');
                outputValue( item, dobj, name, paths, 0 );
            }

            Iterator properties = m_properties.iterator();
            while( properties.hasNext() ) {
                DataCollectionPropertyRenderer property = (DataCollectionPropertyRenderer) properties.next();
                property.render( objects, item );
            }

            Element path = Navigation.newElement("path");
            path.setText(getStableURL(dobj, object));
            item.addContent(path);

            generateItemXML(item, dobj, object, index);

            index++;
            content.addContent(item);
        }

        return content;
    }

    protected String getStableURL(DataObject dobj, ACSObject obj) {
        OID oid = new OID((String)dobj.get(ACSObject.OBJECT_TYPE),
                          dobj.get(ACSObject.ID));
        return Navigation.redirectURL(oid);
    }

    private void outputValue( final Element item, final Object value,
                              final String name,
                              final String[] paths, final int depth ) {
        if( null == value ) return;

        if( value instanceof DataAssociation ) {
            DataAssociation assoc = (DataAssociation) value;
            DataAssociationCursor cursor = assoc.cursor();

            while( cursor.next() ) {
                outputValue( item, cursor.getDataObject(), name, paths, depth );
            }

            cursor.close();
        }

        else if( value instanceof DataObject ) {
            try {
                Object newValue = ((DataObject) value).get( paths[depth] );
                outputValue( item, newValue, name, paths, depth + 1 );
            } catch( PersistenceException ex ) {
                valuePersistenceError( ex, paths, depth );
            }
        }

        else if( depth == paths.length ) {
            Element attribute = Navigation.newElement("attribute");
            attribute.addAttribute("name", name);
            attribute.setText(value.toString());
            if (value instanceof Date) {
                Date date = (Date) value;
                Calendar calDate = Calendar.getInstance();
                calDate.setTime(date);
                attribute.addAttribute("year", Integer.toString(calDate.get(Calendar.YEAR)));
                attribute.addAttribute("month", Integer.toString(calDate.get(Calendar.MONTH)+1));
                attribute.addAttribute("day", Integer.toString(calDate.get(Calendar.DAY_OF_MONTH)));
                attribute.addAttribute("hour", Integer.toString(calDate.get(Calendar.HOUR_OF_DAY)));
                attribute.addAttribute("minute", Integer.toString(calDate.get(Calendar.MINUTE)));
                attribute.addAttribute("second", Integer.toString(calDate.get(Calendar.SECOND)));
            }
            item.addContent(attribute);
        }

        else valuePersistenceError( null, paths, depth );
    }

    private void valuePersistenceError( PersistenceException ex,
                                        String[] paths, int depth ) {
        StringBuffer msg = new StringBuffer();
        msg.append( "Attribute " );
        for( int i=0; i <= depth; i++ ) {
            msg.append( paths[i] );
            if( i != depth ) msg.append( '.' );
        }
        msg.append( " doesn't exist" );

        if( null == ex ) s_log.warn( msg.toString() );
        else s_log.warn( msg.toString(), ex );
    }

    protected void generateItemXML(Element item,
                                   DataObject dobj,
                                   ACSObject obj,
                                   int index) {
    }
}
