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
package com.arsdigita.toolbox.ui;

import com.arsdigita.bebop.PageState;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * 
 *
 * @author Justin Ross
 * @version $Id: DefaultItemDetailModel.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class DefaultItemDetailModel implements ItemDetailModel {
    public static final String versionId = "$Id: DefaultItemDetailModel.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private String m_title;
    private List m_properties;
    private List m_actions;

    public DefaultItemDetailModel() {
        m_properties = new ArrayList();
        m_actions = new ArrayList();
    }

    public String getTitle(PageState pageState) {
        return m_title;
    }

    public void setTitle(String title) {
        m_title = title;
    }

    public Iterator getProperties(PageState pageState) {
        return m_properties.iterator();
    }

    public void addProperty(ItemDetailProperty property) {
        m_properties.add(property);
    }

    public void addProperty(String name, String value) {
        m_properties.add(new ItemDetailProperty(name, value));
    }

    public Iterator getActions(PageState pageState) {
        return m_actions.iterator();
    }

    public void addAction(ItemDetailAction action) {
        m_actions.add(action);
    }

    public void addAction(String name, String url) {
        m_actions.add(new ItemDetailAction(name, url));
    }
}
