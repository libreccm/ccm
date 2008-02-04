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
package com.arsdigita.cms.ui.workflow;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.workflow.simple.Workflow;

import java.math.BigDecimal;

class ItemWorkflowSelectionModel extends ParameterSingleSelectionModel {
    ItemSelectionModel m_itemSel;
    public ItemWorkflowSelectionModel(BigDecimalParameter itemIdParameter) {
        super(itemIdParameter);
        m_itemSel = new ItemSelectionModel("com.arsdigita.cms.ContentItem",
                                           ContentItem.BASE_DATA_OBJECT_TYPE,
                                           itemIdParameter);
    }
    public ItemWorkflowSelectionModel(ItemSelectionModel m) {
        super(m.getStateParameter());
        m_itemSel = m;
    }

    public Object getSelectedKey(PageState state) {
        return Workflow.getObjectWorkflowID
            ((BigDecimal)super.getSelectedKey(state));
    }

    public ItemSelectionModel getItemSelectionModel() {
        return m_itemSel;
    }
}
