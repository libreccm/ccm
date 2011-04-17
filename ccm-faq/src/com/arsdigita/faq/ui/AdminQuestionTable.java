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


import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;

import com.arsdigita.persistence.DataCollection;

import com.arsdigita.util.LockableImpl;

import java.math.BigDecimal;

class AdminQuestionTable extends Table {
    public static final String versionId = "$Id: //apps/faq/dev/src/com/arsdigita/faq/ui/AdminQuestionTable.java#3 $ by $Author: dennis $, $DateTime: 2004/08/17 23:26:27 $";

    private final static String[] HEADERS = {"#", "Question"};

    public AdminQuestionTable(TableModelBuilder builder) {
        super(builder, HEADERS);
        setDefaultCellRenderer(new QuestionCellRenderer());
    }


    static class QuestionModelBuilder extends LockableImpl
        implements TableModelBuilder {

        public TableModel makeModel(Table t, PageState s) {
            return new QuestionModel(s);
        }
    }

    private static class QuestionModel implements TableModel {
        private DataCollection m_query = null;

        public QuestionModel(PageState s) {
            m_query = ((FaqPage)s.getPage()).getFaq(s)
                .getQAPairs().getDataCollection();
        }

        public int getColumnCount() { return HEADERS.length; }

        public Object getKeyAt(int columnIndex) {
            return (BigDecimal)(m_query.get("id"));
        }

        public Object getElementAt(int columnIndex) {
            Object ret = null;

            switch (columnIndex) {
            case 0:
                ret = m_query.get("sortKey");
                break;

            case 1:
                ret = m_query.get("question");
                break;

            default:
                ret = "filler";
            }

            return ret;
        }

        public boolean nextRow() {
            return m_query.next();
        }

    }

    private static class QuestionCellRenderer extends DefaultTableCellRenderer {
        public Component getComponent(Table table, PageState state,
                                      Object value, boolean isSelected,
                                      Object key, int row, int column) {

            switch (column) {
            case 0:
                return super.getComponent(table, state, value,
                                          isSelected, key, row, column);
            case 1:
                return new ControlLink(value.toString());

            default:
                return null;
            }
        }
    }

}
