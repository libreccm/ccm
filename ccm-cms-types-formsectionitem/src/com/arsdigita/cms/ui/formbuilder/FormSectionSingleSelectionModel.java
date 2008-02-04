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


import com.arsdigita.bebop.AbstractSingleSelectionModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.formbuilder.FormSectionItem;
import com.arsdigita.cms.util.GlobalizationUtil;


public class FormSectionSingleSelectionModel extends AbstractSingleSelectionModel {

    private ItemSelectionModel m_item;

    public FormSectionSingleSelectionModel(ItemSelectionModel item) {
        m_item = item;
    }

    public ItemSelectionModel getItemModel() {
        return m_item;
    }

    public boolean isSelected(PageState state) {
        return m_item.isSelected(state);
    }

    public Object getSelectedKey(PageState state) {
        FormSectionItem item = (FormSectionItem)m_item.getSelectedObject(state);
        return item.getFormSection().getID();
    }

    public void setSelectedKey(PageState state,
                               Object key) {
        throw new RuntimeException( (String) GlobalizationUtil.globalize("cms.ui.formbuilder.oh_no_you_dont").localize());
    }

    public void clearSelection(PageState state) {
        throw new RuntimeException( (String) GlobalizationUtil.globalize("cms.ui.formbuilder.oh_no_you_dont").localize());
    }

    public void addChangeListener(ChangeListener l) {
        throw new RuntimeException( (String) GlobalizationUtil.globalize("cms.ui.formbuilder.oh_no_you_dont").localize());
    }

    public void removeChangeListener(ChangeListener l) {
        throw new RuntimeException( (String) GlobalizationUtil.globalize("cms.ui.formbuilder.oh_no_you_dont").localize());
    }

    public ParameterModel getStateParameter() {
        throw new RuntimeException( (String) GlobalizationUtil.globalize("cms.ui.formbuilder.oh_no_you_dont").localize());
    }
}
