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
package com.arsdigita.toolbox.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * DomainTable
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 */

public abstract class DomainTable extends Table {

    private DomainColumn m_key;
    private List m_columns = new ArrayList();
    private TableColumnModel m_columnModel;

    public DomainTable() {
        setModelBuilder(new TableModelBuilder() {
                public TableModel makeModel(Table t, PageState ps) {
                    return DomainTable.this.makeModel(ps);
                }

                public void lock() {}
                public boolean isLocked() { return true; }
            });
        m_columnModel = getColumnModel();
    }

    public void setKey(DomainColumn key) {
        m_key = key;
    }

    public TableColumn add(DomainColumn column) {
        TableColumn tc = new TableColumn(m_columns.size(),
                                         column.getName(),
                                         column.getKey());
        m_columnModel.add(tc);
        m_columns.add(column);

        tc.setCellRenderer(new DefaultTableCellRenderer(column.isActive()));
        tc.setHeaderRenderer(
                             new DefaultTableCellRenderer(column.isSortable())
                             );

        return tc;
    }

    private TableModel makeModel(final PageState ps) {
        SingleSelectionModel ss = getColumnSelectionModel();

        if (ss.isSelected(ps)) {
            int index = ((Integer) ss.getSelectedKey(ps)).intValue();
            DomainColumn dc = (DomainColumn) m_columns.get(index);
            dc.order(ps);
        }

        m_key.order(ps);

        return new TableModel() {
                public int getColumnCount() {
                    return m_columns.size();
                }

                public boolean nextRow() {
                    return DomainTable.this.next(ps);
                }

                public Object getElementAt(int index) {
                    DomainColumn dc = (DomainColumn) m_columns.get(index);
                    return dc.get(ps);
                }

                public Object getKeyAt(int index) {
                    return m_key.get(ps);
                }
            };
    }

    public abstract boolean next(PageState ps);

}
