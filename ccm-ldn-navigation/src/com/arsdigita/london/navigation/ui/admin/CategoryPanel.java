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

package com.arsdigita.london.navigation.ui.admin;

import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.london.navigation.Navigation;

public class CategoryPanel extends SimpleContainer {
    
    private CategorySelectionModel m_category;
    private CategoryTree m_tree;
    private CategoryFormAddContext m_addForm;
    private CategoryForm m_form;
    private QuickLinkPanel m_links;

    public CategoryPanel() {
        super(Navigation.NAV_PREFIX + ":categoryPanel",
              Navigation.NAV_NS);
        
        m_category = new CategorySelectionModel();
        m_category.addChangeListener(
            new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    PageState state = e.getPageState();
                    boolean selected = m_category.isSelected(state);
                    m_addForm.setVisible(state, selected);
                    m_form.setVisible(state, selected);
                    m_links.setVisible(state, selected);
                }
            });

        m_tree = new CategoryTree(m_category);
        m_addForm = new CategoryFormAddContext(m_category);
        m_form = new CategoryForm(m_category);
        m_links = new QuickLinkPanel(m_category);

        add(m_tree);
        add(m_addForm);
        add(m_form);
        add(m_links);
    }

    public void register(Page p) {
        super.register(p);
        
        p.setVisibleDefault(m_addForm, false);
        p.setVisibleDefault(m_form, false);
        p.setVisibleDefault(m_links, false);
    }
}
