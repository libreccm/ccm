/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.formbuilder;


import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.workflow.WorkflowLockedContainer;
import com.arsdigita.formbuilder.ui.ControlEditor;


/**
 * 
 * 
 */
public class FormSectionControls extends ControlEditor {

    private ItemSelectionModel m_itemModel;

    /**
     * Constructor. 
     * 
     * @param item
     * @param parent 
     */
    public FormSectionControls(ItemSelectionModel item,
                               AuthoringKitWizard parent) {

        super("forms-cms",
              new FormSectionSingleSelectionModel(item));

        m_itemModel = item;
    }

    /**
     * 
     * @param container
     * @param child 
     */
    @Override
    protected void addEditableComponent(Container container,
                                        Component child) {
        WorkflowLockedContainer lock = new 
            WorkflowLockedContainer(((FormSectionSingleSelectionModel)getFormModel())
                                    .getItemModel());
        lock.add(child);
        super.addEditableComponent(container, lock);
    }

    /**
     * 
     * @param state
     * @return 
     */
    @Override
    protected boolean addItemEditObserver(PageState state) {
        return CMS.getSecurityManager(state).canAccess(
            state.getRequest(),
            SecurityManager.EDIT_ITEM,
            (ContentItem) m_itemModel.getSelectedObject(state));
    }
}
