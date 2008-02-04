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


import com.arsdigita.bebop.List;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.PageState;

import com.arsdigita.bebop.parameters.IntegerParameter;

import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.formbuilder.PersistentFormSection;
import com.arsdigita.formbuilder.PersistentProcessListener;


import com.arsdigita.util.LockableImpl;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;

import java.util.Iterator;


public class ProcessListenerListModelBuilder extends LockableImpl implements ListModelBuilder {
    public ListModel makeModel(List l, PageState state) {
        Integer form_id = (Integer)state.getValue(new IntegerParameter("form"));
        PersistentFormSection form = null;
        try {
            form = new PersistentFormSection(new BigDecimal(form_id.intValue()));
        } catch (DataObjectNotFoundException ex) {
            throw new UncheckedWrapperException(ex);
        }

        return new ProcessListenerListModel(form.getProcessListeners());
    }

    private class ProcessListenerListModel implements ListModel {
        private Iterator m_listeners;
        private PersistentProcessListener listener;

        public ProcessListenerListModel(Iterator listeners) {
            m_listeners = listeners;
            listener = null;
        }

        public boolean next() {
            if (m_listeners.hasNext()) {
                listener = (PersistentProcessListener)m_listeners.next();
                return true;
            }
            return false;
        }

        public Object getElement() {
            return listener;
        }

        public String getKey() {
            return listener.getID().toString();
        }
    }
}
