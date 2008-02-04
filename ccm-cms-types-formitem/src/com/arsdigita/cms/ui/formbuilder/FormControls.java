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
package com.arsdigita.cms.ui.formbuilder;


import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.formbuilder.FormSectionItem;
import com.arsdigita.cms.formbuilder.FormSectionWrapper;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.workflow.WorkflowLockedContainer;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.ui.ControlEditor;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;

public class FormControls extends ControlEditor {
    private ItemSelectionModel m_itemModel;

    public FormControls(ItemSelectionModel item,
                        AuthoringKitWizard parent) {
        super("forms-cms",
              new FormSingleSelectionModel(item), true);

        m_itemModel = item;
        
        setFormSectionModelBuilder(new FormSectionModelBuilder(item));
    }

    protected void addEditableComponent(Container container,
                                        Component child) {
        WorkflowLockedContainer lock = new WorkflowLockedContainer(((FormSingleSelectionModel)getFormModel()).getItemModel());
        lock.add(child);
        super.addEditableComponent(container, lock);
    }

    protected PersistentComponent getFormSection(PageState state,
                                                 BigDecimal sectionID) {        
        FormSectionItem section = null;
        try {
            section = (FormSectionItem)DomainObjectFactory.newInstance(
                new OID(FormSectionItem.BASE_DATA_OBJECT_TYPE,
                        sectionID));
        } catch (DataObjectNotFoundException ex) {
            throw new UncheckedWrapperException("cannot load section", ex);
        }
        
        FormSectionWrapper wrapper = FormSectionWrapper.create(section,
                                                               ContentItem.DRAFT);
        
        return wrapper;
    }
    
    
    protected boolean addItemEditObserver(PageState state) {
        return Utilities.getSecurityManager(state).canAccess(
            state.getRequest(),
            SecurityManager.EDIT_ITEM,
            (ContentItem) m_itemModel.getSelectedObject(state));
    }
}
