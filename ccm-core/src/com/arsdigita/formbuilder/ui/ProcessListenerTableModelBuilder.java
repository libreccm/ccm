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

import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.PageState;
import com.arsdigita.formbuilder.PersistentFormSection;
import java.math.BigDecimal;
import com.arsdigita.bebop.table.TableModel;
import java.util.Iterator;
import com.arsdigita.formbuilder.PersistentProcessListener;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.util.UncheckedWrapperException;



public class ProcessListenerTableModelBuilder extends AbstractTableModelBuilder {
    SingleSelectionModel m_selection;

    public ProcessListenerTableModelBuilder(SingleSelectionModel selection) {
        m_selection = selection;
    }

    public TableModel makeModel(Table table,
                                PageState state) {
        PersistentFormSection form = null;
        try {
            form = new PersistentFormSection((BigDecimal)m_selection.getSelectedKey(state));
        } catch (DataObjectNotFoundException ex) {
            ex.printStackTrace();
            throw new UncheckedWrapperException(ex);
        }

        return new PersistentFormTableModel(form.getProcessListeners());
    }

    private class PersistentFormTableModel implements TableModel {
        private Iterator m_listeners;
        private PersistentProcessListener listener;

        public PersistentFormTableModel(Iterator listeners) {
            m_listeners = listeners;
        }


        public boolean nextRow() {
            if (m_listeners.hasNext()) {
                listener = (PersistentProcessListener)m_listeners.next();
                return true;
            }
            return false;
        }

        public int getColumnCount() {
            return 3;
        }

        public Object getKeyAt(int columnIndex) {
            return listener.getID();
        }

        public Object getElementAt(int columnIndex) {
            return listener;
        }
    }
}
