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

package com.arsdigita.navigation.ui.admin;

import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.navigation.Navigation;
import com.arsdigita.ui.admin.applications.ApplicationInstanceAwareContainer;

/**
 * Panel for managing the JSP templates assigned to categories. The templates are
 * describing which components and which data is rendered when accessing a category
 * using the navigation application.
 * 
 * @author Unknown
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class CategoryPanel extends SimpleContainer {
    
    private final CategorySelectionModel category;
    private final CategoryTree tree;
    private final CategoryFormAddContext addForm;
    private final CategoryForm form;
    private final QuickLinkPanel links;

    public CategoryPanel() {
        super(Navigation.NAV_PREFIX + ":categoryPanel",
              Navigation.NAV_NS);
        
        category = new CategorySelectionModel();
        category.addChangeListener(
            new ChangeListener() {
                @Override
                public void stateChanged(final ChangeEvent event) {
                    final PageState state = event.getPageState();
                    final boolean selected = category.isSelected(state);
                    addForm.setVisible(state, selected);
                    form.setVisible(state, selected);
                    links.setVisible(state, selected);
                }
            });

        tree = new CategoryTree(category);
        addForm = new CategoryFormAddContext(category);
        form = new CategoryForm(category);
        links = new QuickLinkPanel(category);

        add(tree);
        add(addForm);
        add(form);
        add(links);
    }

    public CategoryPanel(final ApplicationInstanceAwareContainer parent) {
        this();
        
        tree.setParent(parent);
    }
    
    @Override
    public void register(final Page page) {
        super.register(page);
        
        page.setVisibleDefault(addForm, false);
        page.setVisibleDefault(form, false);
        page.setVisibleDefault(links, false);
    }
}
