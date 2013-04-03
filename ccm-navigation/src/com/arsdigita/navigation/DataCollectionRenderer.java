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
package com.arsdigita.navigation;

import com.arsdigita.categorization.Categorization;
import com.arsdigita.categorization.Category;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.util.Assert;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.util.StringUtils;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.log4j.Logger;

public class DataCollectionRenderer extends LockableImpl {

    private static final Logger s_log = Logger.getLogger(DataCollectionRenderer.class);
    private ArrayList m_attributes = new ArrayList();
    private ArrayList m_properties = new ArrayList();
    private int m_pageSize = 20;
    private boolean m_specializeObjects = false;
    /**
     * The traversal adapter context used if {@link #m_specializeObjects} is
     * <code>true</code>. Defaults to
     * {@link SimpleXMLGenerator.ADAPTER_CONTEXT}.
     */
    private String m_specializeObjectsContext = SimpleXMLGenerator.ADAPTER_CONTEXT;
    private boolean m_wrapAttributes = false;
    private boolean m_navItems = true;

    public DataCollectionRenderer() {
        addAttribute("objectType");
        addAttribute("id");
    }

    public void addAttribute(final String name) {
        Assert.isUnlocked(this);
        m_attributes.add(name);
    }

    public void addProperty(final DataCollectionPropertyRenderer pr) {
        Assert.isUnlocked(this);
        m_properties.add(pr);
    }

    protected int getPageSize() {
        return m_pageSize;
    }

    public void setPageSize(final int pageSize) {
        Assert.isUnlocked(this);
        m_pageSize = pageSize;
    }

    protected boolean getNavItems() {
        return m_navItems;
    }

    /**
     * Specify whether to include the items for navigation that are within
     * same category.
     * This flag toggles the generation of nav:item xml elements.
     */
    public void setNavItems(final boolean navItems) {
        m_navItems = navItems;
    }

    protected boolean isSpecializeObjects() {
        return m_specializeObjects;
    }

    public void setSpecializeObjects(final boolean specializeObjects) {
        Assert.isUnlocked(this);
        m_specializeObjects = specializeObjects;
    }

    protected String getSpecializeObjectsContext() {
        return m_specializeObjectsContext;
    }

    /**
     * Sets the context of the traversal adapter used the render the objects
     * if {@link #m_specializeObjects} is set to
     * <code>true</code>
     *
     * @param context The adapter context.
     */
    public void setSpecializeObjectsContext(final String context) {
        m_specializeObjectsContext = context;
    }

    public void setWrapAttributes(final boolean wrapAttributes) {
        Assert.isUnlocked(this);
        m_wrapAttributes = wrapAttributes;
    }

    public List getAttributes() {
        return m_attributes;
    }

    public List getProperties() {
        return m_properties;
    }

    /**
     * @param objects
     * @param pageNum      
     *
     * @return
     */
    public Element generateXML(final DataCollection objects, final int pageNum) {
        Assert.isLocked(this);

        int pageNumber = pageNum;

        // Quasimodo: Begin
        // If objects is null or empty, do not insert objectList-element
        // but do insert noContent-element and return immediately
        if (objects == null || objects.isEmpty()) {
            return Navigation.newElement("noContent");
        }
        // Quasimodo: End

        final Element content = Navigation.newElement("objectList");

        //Return the empty nav:item & nav:paginator tags.
        // Quasimodo: Why should I??? There is no need for a paginator if there aren't any elements
        if (!m_navItems) {
            final Element paginator = Navigation.newElement("paginator");
            content.addContent(paginator);
            return content;
        }

        final long objectCount = objects.size();
        final int pageCount = (int) Math.ceil((double) objectCount / (double) m_pageSize);

        if (pageNumber < 1) {
            pageNumber = 1;
        }

        if (pageNumber > pageCount) {
            pageNumber = (pageCount == 0 ? 1 : pageCount);
        }

        final long begin = ((pageNumber - 1) * m_pageSize);
        final int count = (int) Math.min(m_pageSize, (objectCount - begin));
        final long end = begin + count;

        if (count != 0) {
            objects.setRange((int) begin + 1, (int) end + 1);
        }

        final Element paginator = Navigation.newElement("paginator");

        // Quasimodo: Begin
        // Copied from com.arsdigita.search.ui.ResultPane
        final String pageParam = "pageNumber";

        final URL url = Web.getContext().getRequestURL();
        final ParameterMap map = new ParameterMap();

        if (url.getParameterMap() != null) {
            final Iterator current = url.getParameterMap().keySet().iterator();
            while (current.hasNext()) {
                final String key = (String) current.next();
                if (key.equals(pageParam)) {
                    continue;
                }
                map.setParameterValues(key, url.getParameterValues(key));
            }
        }

        paginator.addAttribute("pageParam", pageParam);
        paginator.addAttribute("baseURL", URL.there(url.getPathInfo(), map).
                toString());
        // Quasimodo: End

        paginator.addAttribute("pageNumber", Long.toString(pageNumber));
        paginator.addAttribute("pageCount", Long.toString(pageCount));
        paginator.addAttribute("pageSize", Long.toString(m_pageSize));
        paginator.addAttribute("objectBegin", Long.toString(begin + 1));
        paginator.addAttribute("objectEnd", Long.toString(end));
        paginator.addAttribute("objectCount", Long.toString(objectCount));

        content.addContent(paginator);

        int index = 0;
        while (objects.next()) {
            final DataObject dobj = objects.getDataObject();
            ACSObject object = null;
            if (m_specializeObjects) {
                object = (ACSObject) DomainObjectFactory.newInstance(dobj);
                if (object == null) {
                    s_log.error(String.format("Failed to specialize object with with id %s. Skiping object.", dobj.
                            getOID().toString()));
                    continue;
                } else {
                    s_log.debug("Specializing successful.");
                }
            }
            final Element item = Navigation.newElement(content, "item");

            // Create a canEdit link if the current user is permitted to edit the item. 
            // Note: Works only when list specializes the object because an ACSObject instance is needed for creating
            // the PermissionDescriptor.
            Party currentParty = Kernel.getContext().getParty();
            if (currentParty == null) {
                currentParty = Kernel.getPublicUser();
            }
            final PermissionDescriptor edit;
            if (!m_specializeObjects || object == null) {
                edit = new PermissionDescriptor(PrivilegeDescriptor.get(
                        com.arsdigita.cms.SecurityManager.CMS_EDIT_ITEM), dobj.getOID(), currentParty.getOID());
            } else {
                edit = new PermissionDescriptor(PrivilegeDescriptor.get(
                        com.arsdigita.cms.SecurityManager.CMS_EDIT_ITEM), object, currentParty);                
            }
            if (PermissionService.checkPermission(edit)) {
                item.addAttribute("canEdit", "true");
            }

            final Iterator attributes = m_attributes.
                    iterator();
            while (attributes.hasNext()) {
                final String name = (String) attributes.next();
                final String[] paths = StringUtils.split(name, '.');
                outputValue(item, dobj, name, paths, 0);
            }

            final Iterator properties = m_properties.iterator();
            while (properties.hasNext()) {
                final DataCollectionPropertyRenderer property = (DataCollectionPropertyRenderer) properties.next();
                property.render(objects, item);
            }

            final Element path = Navigation.newElement(item, "path");
            path.setText(getStableURL(dobj, object));
            //item.addContent(path);

            generateItemXML(item, dobj, object, index);

            index++;
            //content.addContent(item);
        }

        return content;
    }

    protected String getStableURL(final DataObject dobj, final ACSObject obj) {
        final OID oid = new OID((String) dobj.get(ACSObject.OBJECT_TYPE), dobj.get(ACSObject.ID));
        return Navigation.redirectURL(oid);
    }

    protected void outputValue(final Element item,
                               final Object value,
                               final String name,
                               final String[] paths,
                               final int depth) {
        if (null == value) {
            return;
        }

        if (value instanceof DataAssociation) {
            final DataAssociation assoc = (DataAssociation) value;
            final DataAssociationCursor cursor = assoc.cursor();

            while (cursor.next()) {
                outputValue(item, cursor.getDataObject(), name, paths, depth);
            }

            cursor.close();
        } else if (value instanceof DataObject) {
            try {
                final Object newValue = ((DataObject) value).get(paths[depth]);
                outputValue(item, newValue, name, paths, depth + 1);
            } catch (PersistenceException ex) {
                valuePersistenceError(ex, paths, depth);
            }
        } else if (depth == paths.length) {
            final Element attribute = Navigation.newElement("attribute");
            attribute.addAttribute("name", name);
            attribute.setText(value.toString());

            // Special handling of Date - see ccm-core/src/com/arsdigita/domain/DomainObjectXMLRenderer.java
            if (value instanceof Date) {
                final Date date = (Date) value;
                final Calendar calDate = Calendar.getInstance();
                calDate.setTime(date);
                attribute.addAttribute("year", Integer.toString(calDate.get(Calendar.YEAR)));
                attribute.addAttribute("month", Integer.toString(calDate.get(Calendar.MONTH) + 1));
                attribute.addAttribute("day", Integer.toString(calDate.get(Calendar.DAY_OF_MONTH)));
                attribute.addAttribute("hour", Integer.toString(calDate.get(Calendar.HOUR_OF_DAY)));
                attribute.addAttribute("minute", Integer.toString(calDate.get(Calendar.MINUTE)));
                attribute.addAttribute("second", Integer.toString(calDate.get(Calendar.SECOND)));

                // Quasimodo: BEGIN
                // Add attributes for date and time
                final Locale negLocale = com.arsdigita.globalization.GlobalizationHelper.getNegotiatedLocale();
                final DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM, negLocale);
                final DateFormat longDateFormatter = DateFormat.getDateInstance(DateFormat.LONG, negLocale);
                final DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT, negLocale);
                attribute.addAttribute("date", dateFormatter.format(date));
                attribute.addAttribute("longDate", longDateFormatter.format(date));
                attribute.addAttribute("time", timeFormatter.format(date));
                attribute.addAttribute("monthName", calDate.getDisplayName(Calendar.MONTH, Calendar.LONG, negLocale));
                // Quasimodo: END

            }
            item.addContent(attribute);
        } else {
            valuePersistenceError(null, paths, depth);
        }
    }

    private void valuePersistenceError(final PersistenceException exception, final String[] paths, final int depth) {
        final StringBuffer msg = new StringBuffer(30);
        msg.append("Attribute ");
        for (int i = 0; i <= depth; i++) {
            msg.append(paths[i]);
            if (i != depth) {
                msg.append('.');
            }
        }
        msg.append(" doesn't exist");

        if (null == exception) {
            s_log.warn(msg.toString());
        } else {
            s_log.warn(msg.toString(), exception);
        }
    }

    protected void generateItemXML(final Element item, final DataObject dobj, final ACSObject obj, final int index) {
    }

}
