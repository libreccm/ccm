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
package com.arsdigita.faq.ui;

import com.arsdigita.faq.Faq;
import com.arsdigita.faq.QAPair;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;

import com.arsdigita.persistence.DataCollection;

import com.arsdigita.util.LockableImpl;

class QAList extends List {

    public static final String versionId = "$Id: //apps/faq/dev/src/com/arsdigita/faq/ui/QAList.java#3 $ by $Author: dennis $, $DateTime: 2004/08/17 23:26:27 $";

    public QAList() {
        super();
        setModelBuilder(new QAListModelBuilder());
        setCellRenderer(new QACellRenderer());
    }

    private class QAListModelBuilder extends LockableImpl
        implements ListModelBuilder {

        public ListModel makeModel(List l, PageState s) {
            return new QAListModel(s);
        }
    }

    private class QAListModel implements ListModel {
        private DataCollection m_query;

        public QAListModel(PageState s) {
            Faq faq = ((FaqPage)s.getPage()).getFaq(s);
            m_query = faq.getQAPairs().getDataCollection();
        }

        public Object getElement() {
            return new QAPair(m_query.getDataObject());
        }

        public String getKey() {
            return m_query.get("id").toString();
        }

        public boolean next() {
            return m_query.next();
        }
    }

    private class QACellRenderer implements ListCellRenderer {
        public Component getComponent(List list, PageState state,
                                      Object value, String key,
                                      int index, boolean isSelected) {
            return new QAView((QAPair) value);
        }
    }

}
