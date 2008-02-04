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

package com.arsdigita.london.navigation.ui.portlet;

import com.arsdigita.london.navigation.portlet.ItemListPortlet;

import com.arsdigita.bebop.parameters.ParameterModel;

import com.arsdigita.bebop.RequestLocal;

import com.arsdigita.kernel.ResourceType;

import com.arsdigita.util.UncheckedWrapperException;

import java.util.TooManyListenersException;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;

import com.arsdigita.portal.Portlet;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeCollection;


public class ItemListPortletEditor extends ObjectListPortletEditor {

    private SingleSelect m_version;

    public ItemListPortletEditor(ResourceType resType,
                                 RequestLocal parentAppRL) {
        super(resType, parentAppRL);
    }
    
    public ItemListPortletEditor(RequestLocal application) {
        super(application);
    }

    protected void addWidgets() {
        super.addWidgets();

        m_version = new SingleSelect(new StringParameter("version"));
        m_version.addOption(new Option(ContentItem.DRAFT,
                                       "Draft"));
        m_version.addOption(new Option(ContentItem.LIVE,
                                       "Live"));
        add(new Label("Version:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_version);
    }

    protected void initWidgets(PageState state,
                               Portlet portlet)
        throws FormProcessException {
        super.initWidgets(state, portlet);
        
        if (portlet != null) {
            ItemListPortlet myportlet = (ItemListPortlet)portlet;
            m_version.setValue(state, myportlet.getVersion());
        } else {
            m_version.setValue(state, ContentItem.LIVE);
        }
    }

    protected void processWidgets(PageState state,
                                  Portlet portlet)
        throws FormProcessException {
        super.processWidgets(state, portlet);
        
        ItemListPortlet myportlet = (ItemListPortlet)portlet;
        myportlet.setVersion((String)m_version.getValue(state));
    }
    
    protected PrintListener getBaseObjectTypes() {
        return new ItemTypePrintListener(true);
    }

    protected PrintListener getRestrictedObjectTypes() {
        return new ItemTypePrintListener(false);
    }

    public static class ItemTypePrintListener implements PrintListener {

        private boolean m_all;

        public ItemTypePrintListener(boolean all) {
            m_all = all;
        }
        
        public void prepare(PrintEvent ev) {
            OptionGroup target = (OptionGroup)ev.getTarget();
            
            ContentTypeCollection types = ContentType.getRegisteredContentTypes();
            types.addOrder(ContentType.LABEL);

            target.addOption(new Option(null, "-- select type --"));
            if (m_all) {
                target.addOption(new Option(ContentPage.BASE_DATA_OBJECT_TYPE, 
                                            "All content"));
            }
            
            while (types.next()) {
                ContentType type = types.getContentType();
                
                target.addOption(new Option(type.getAssociatedObjectType(),
                                            type.getLabel()));
            }
        }
    }

}
