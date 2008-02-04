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

package com.arsdigita.london.portal.portlet;

import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.categorization.Category;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.london.portal.ui.portlet.ContentDirectoryPortletRenderer;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.portal.Portlet;

public class ContentDirectoryPortlet extends Portlet {
    public static final String versionId = "$Id: ContentDirectoryPortlet.java 1174 2006-06-14 14:14:15Z fabrice $ by $Author: fabrice $, $DateTime: 2003/08/08 05:59:54 $";

	public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.london.portal.portlet.ContentDirectoryPortlet";

    public static final String ROOT = "root";

    public static final String LAYOUT = "layout";

    public static final String DEPTH = "depth";

    public static final String LAYOUT_GRID = "grid";

    public static final String LAYOUT_PANEL = "panel";

    public ContentDirectoryPortlet(DataObject dataObject) {
        super(dataObject);
    }

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    protected AbstractPortletRenderer doGetPortletRenderer() {
        return new ContentDirectoryPortletRenderer(this);
    }

    public void setRoot(Category root) {
        setAssociation(ROOT, root);
    }

    public Category getRoot() {
        DataObject dobj = (DataObject)get(ROOT);
        if (dobj == null) {
            return null;
        }
        return (Category)DomainObjectFactory.newInstance(dobj);
    }

    public void setLayout(String layout) {
        set(LAYOUT, layout);
    }

    public String getLayout() {
        return (String)get(LAYOUT);
    }

    public void setDepth(int depth) {
        set(DEPTH, new Integer(depth));
    }

    public int getDepth() {
        return ((Integer)get(DEPTH)).intValue();
    }
}
