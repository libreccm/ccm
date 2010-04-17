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
package com.arsdigita.cms.ui;


import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.toolbox.ui.OIDParameter;
import com.arsdigita.persistence.OID;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.xml.Element;

import com.arsdigita.search.Document;
import com.arsdigita.search.ui.QueryGenerator;
import com.arsdigita.search.ui.ResultsPane;

/**
 * A counterpart to {@link ItemSearchPopup} designed to be 
 * embedded in a form for use when javascript popup is not
 * available.
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: ItemSearchSectionInline.java 1940 2009-05-29 07:15:05Z terry $
 */
public class ItemSearchSectionInline extends ItemSearchSection {

    private static final org.apache.log4j.Logger s_log = 
        org.apache.log4j.Logger.getLogger(ItemSearchSectionInline.class);


    private Submit m_selectItem;
    private String m_name;

    private OIDParameter m_item;

    /**
     * Construct a new <code>ItemSearchSectionInline</code> component
     *
     * @param context the context for the retrieved items. Should be
     *   {@link ContentItem#DRAFT} or {@link ContentItem#LIVE}
     */
    public ItemSearchSectionInline(String name, String context) {
        super(name, context);
        m_name = name;
        m_item = new OIDParameter(name + "_itemOID");
    }

    public void register(Page p) {
        super.register(p);
        p.addGlobalStateParam(m_item);
    }

    protected Component createResultsPane(QueryGenerator generator) {
        return new InlineResultsPane(generator);
    }

    protected void addResultsPane(Container container) {
        super.addResultsPane(container);

        m_selectItem = new ItemSelectSubmit(
            m_name + "_itemSelect", "Select Item");
        container.add(m_selectItem);
    }

    protected void addFormListener() {
        // do nothing (ItemSearchWidget's submission listener does this.
    }

    public void processQuery(PageState state) {
        super.processQuery(state);
        m_selectItem.setVisible(state, hasQuery(state));
    }

    public boolean isItemSelected(PageState state) {
        return m_selectItem.isSelected(state);
    }

    public ContentItem getSelectedItem(PageState state) {
        OID oid = (OID)state.getValue(m_item);
        
        if (oid == null) {
            return null;
        }
        
        return (ContentItem)DomainObjectFactory.newInstance(oid);
    }
    
    private class ItemSelectSubmit extends Submit {
        public ItemSelectSubmit(String name, String label) {
            super(name, label);
        }
        public ItemSelectSubmit(String name, GlobalizedMessage label) {
            super(name, label);
        }

        public boolean isVisible(PageState ps) {
            return hasQuery(ps) && super.isVisible(ps);
        }

    }

    private class InlineResultsPane extends ResultsPane {
        public InlineResultsPane(QueryGenerator query) {
            super(query);
            setRelativeURLs(true);
        }
        
        protected Element generateDocumentXML(PageState state,
                                              Document doc) {
            Element element = super.generateDocumentXML(state, doc);
            
            element.addAttribute("field", m_item.getName());
            element.addAttribute("class", "radioButton");

            return element;
        }
    }
}
