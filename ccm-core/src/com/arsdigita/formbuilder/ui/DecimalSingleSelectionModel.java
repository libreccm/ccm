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
package com.arsdigita.formbuilder.ui;

import com.arsdigita.bebop.AbstractSingleSelectionModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import java.math.BigDecimal;
import com.arsdigita.bebop.SingleSelectionModel;


public class DecimalSingleSelectionModel extends AbstractSingleSelectionModel {

    private SingleSelectionModel m_selection;

    public DecimalSingleSelectionModel(SingleSelectionModel selection) {
        m_selection = selection;
    }

    public boolean isSelected(PageState state) {
        return m_selection.isSelected(state);
    }

    public Object getSelectedKey(PageState state) {
        Object obj = m_selection.getSelectedKey(state);
        return obj == null ? null : new BigDecimal((String)obj);
    }

    public void setSelectedKey(PageState state,
                               Object key) {
        m_selection.setSelectedKey(state,
                                   (key == null ? null :
                                    key.toString()));
    }

    public void clearSelection(PageState state) {
        m_selection.clearSelection(state);
    }

    public void addChangeListener(ChangeListener l) {
        // XXX broken event source
        m_selection.addChangeListener(l);
    }

    public void removeChangeListener(ChangeListener l) {
        // XXX broken event source
        m_selection.removeChangeListener(l);
    }

    public ParameterModel getStateParameter() {
        return m_selection.getStateParameter();
    }
}
