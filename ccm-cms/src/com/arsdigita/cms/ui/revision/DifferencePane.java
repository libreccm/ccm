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
package com.arsdigita.cms.ui.revision;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.table.DefaultTableColumnModel;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.cms.ui.BaseItemPane;
import com.arsdigita.cms.ui.item.ContentItemRequestLocal;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.toolbox.ui.Section;
import com.arsdigita.versioning.Difference;
import com.arsdigita.versioning.Versions;
import org.apache.log4j.Logger;

/** 
 *
 * @version $Id: DifferencePane.java 777 2005-09-12 14:55:15Z fabrice $
 */
final class DifferencePane extends BaseItemPane {

    private static final Logger s_log = Logger.getLogger(DifferencePane.class);

    private final ContentItemRequestLocal m_item;
    private final TransactionRequestLocal m_from;
    private final TransactionRequestLocal m_to;

    DifferencePane(final ContentItemRequestLocal item,
                   final TransactionRequestLocal from,
                   final TransactionRequestLocal to,
                   final ActionLink returnLink) {
        m_item = item;
        m_from = from;
        m_to = to;

        final LayoutPanel panel = new LayoutPanel();
        add(panel);
        setDefault(panel);

        final SimpleContainer container = new SimpleContainer();
        panel.setBody(container);

        final ActionGroup returnGroup = new ActionGroup();
        container.add(returnGroup);

        returnGroup.addAction(returnLink, ActionGroup.RETURN);

        final Section operations = new Section
            (gz("cms.ui.item.revision.operations"));
        container.add(operations);

        operations.setBody(new OperationTable());
    }

    private class OperationTable extends Table {
        private TableColumn m_object = new TableColumn
            (0, lz("cms.ui.item.revision.operation.object"));
        private TableColumn m_property = new TableColumn
            (1, lz("cms.ui.item.revision.operation.property"));
        private TableColumn m_change = new TableColumn
            (2, lz("cms.ui.item.revision.operation.change"));

        public OperationTable() {
            super(null, new DefaultTableColumnModel());

            setModelBuilder(new ModelBuilder());

            final TableColumnModel columns = getColumnModel();
            columns.add(m_object);
            columns.add(m_property);
            columns.add(m_change);

            m_object.setCellRenderer(new DefaultTableCellRenderer());
            m_property.setCellRenderer(new DefaultTableCellRenderer());
            m_change.setCellRenderer(new ChangeCellRenderer());

            setEmptyView(new Label(gz("cms.ui.item.revision.operation.none")));
        }

        private class ChangeCellRenderer implements TableCellRenderer {
            @Override
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row, final int column) {
                final Object[] change = (Object[]) value;
                final GridPanel result = new GridPanel(1);

                if (((Boolean) change[0]).booleanValue()) {
                    final String adds = (String) change[4];
                    final String removes = (String) change[3];

                    if (!adds.equals("")) {
                        final Label addedLabel = new Label
                            (lz("cms.ui.item.revision.added") + " " + adds);
                        result.add(addedLabel);
                    }

                    if (!removes.equals("")) {
                        final Label removedLabel = new Label
                            (lz("cms.ui.item.revision.removed") + " " +
                             removes);
                        result.add(removedLabel);
                    }
                } else {
                    final String from = (String) change[1];
                    final String to = (String) change[2];

                    final Label toLabel = new Label
                        (lz("cms.ui.item.revision.to") + " " + to);
                    result.add(toLabel);

                    final Label fromLabel = new Label
                        (lz("cms.ui.item.revision.from") + " " + from);
                    result.add(fromLabel);

                    // DEPRECATED! bebop is not supposed to specify design
                    // properties but logical qualifications. The direct
                    // speification of color must be placed by a logical
                    // qualification of logical status so that the theme can
                    // decide how to display properly. (propably setStyleAttribut
                    // TODO: Add as a (logical/semantic) qualifier
                    // fromLabel.setColor(Color.gray);
                }

                return result;
            }
        }
    }

    private class ModelBuilder extends AbstractTableModelBuilder {
        @Override
        public final TableModel makeModel(final Table table,
                                          final PageState state) {

            final Difference diff = Versions.diff
                (m_item.getContentItem(state).getOID(),
                 m_from.getTransaction(state),
                 m_to.getTransaction(state));

            return new Model(diff);
        }

        private class Model implements TableModel {
            private final OperationIterator m_ops;
            private Object[] m_op;

            Model(final Difference difference) {
                m_ops = new OperationIterator(difference);
            }

            @Override
            public final int getColumnCount() {
                return 3;
            }

            @Override
            public final boolean nextRow() {
                if (m_ops.hasNext()) {
                    m_op = m_ops.next();

                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public final Object getElementAt(final int column) {
                switch (column) {
                case 0:
                    return m_op[0];
                case 1:
                    return m_op[1];
                case 2:
                    return new Object[] {
                        m_op[2], m_op[3], m_op[4], m_op[5], m_op[6]
                    };
                default:
                    throw new IllegalStateException();
                }
            }

            @Override
            public final Object getKeyAt(final int column) {
                return null;
            }
        }
    }
}
