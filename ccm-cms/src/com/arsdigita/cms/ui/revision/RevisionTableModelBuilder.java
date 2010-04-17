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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.cms.ui.item.ContentItemRequestLocal;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.OID;
import com.arsdigita.toolbox.ui.FormatStandards;
import com.arsdigita.versioning.TagCollection;
import com.arsdigita.versioning.Transaction;
import com.arsdigita.versioning.TransactionCollection;
import com.arsdigita.versioning.Versions;
import org.apache.log4j.Logger;

/**
 * @author Stanislav Freidin &lt;sfreidin@redhat.com&gt;
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: RevisionTableModelBuilder.java 1942 2009-05-29 07:53:23Z terry $
 */
class RevisionTableModelBuilder extends AbstractTableModelBuilder {

    private static final Logger s_log = Logger.getLogger
        (RevisionTableModelBuilder.class);

    static final int FROM = 0;
    static final int TO = 1;
    static final int TIMESTAMP = 2;
    static final int USER = 3;
    static final int DESCRIPTION = 4;
    static final int PREVIEW = 5;
    static final int ROLLBACK = 6;

    static final int COLUMNS = 7;

    private final ContentItemRequestLocal m_item;

    RevisionTableModelBuilder(final ContentItemRequestLocal item) {
        m_item = item;
    }

    public final TableModel makeModel(final Table table,
                                      final PageState state) {
        return new Model(m_item.getContentItem(state).getOID());
    }

    private static class Model implements TableModel {
        private final TransactionCollection m_transactions;
        private Transaction m_transaction;
        private long m_count = 0;
        private long m_last = 2;

        public Model(final OID oid) {
            m_transactions = Versions.getTaggedTransactions(oid);
            m_last = m_transactions.size() + 2;
        }

        public final int getColumnCount() {
            return COLUMNS;
        }

        public final boolean nextRow() {
            m_count++;

            if (m_count == 1) {
                return true;
            } else if (m_count == m_last) {
                return true;
            } else if (m_transactions.next()) {
                m_transaction = m_transactions.getTransaction();

                return true;
            } else {
                m_transactions.close();

                return false;
            }
        }

        public final Object getElementAt(final int column) {
            if (m_count == 1) {
                switch (column) {
                case TIMESTAMP:
                    return lz("cms.ui.item.revision.current");
                default:
                    return "";
                }
            } else if (m_count == m_last) {
                switch (column) {
                case TIMESTAMP:
                    return lz("cms.ui.item.revision.first");
                default:
                    return "";
                }
            } else {
                switch (column) {
                case TIMESTAMP:
                    return FormatStandards.formatDateTime
                        (m_transaction.getTimestamp());
                case USER:
                    final User user = m_transaction.getUser();

                    if (user == null) {
                        return "";
                    } else {
                        return user.getPrimaryEmail().toString();
                    }
                case DESCRIPTION:
                    final TagCollection tags = m_transaction.getTags();
                    final StringBuffer buffer = new StringBuffer();
                    final char sep = '\n';

                    while (tags.next()) {
                        buffer.append(tags.getTag().getDescription() + sep);
                    }

                    final int end = buffer.length() - 1;

                    if (buffer.charAt(end) == sep) {
                        return buffer.substring(0, end);
                    } else {
                        return buffer.toString();
                    }
                default:
                    return "";
                }
            }
        }

        public final Object getKeyAt(final int column) {
            if (m_count == 1) {
                return "first";
            } else if (m_count == m_last) {
                return "last";
            } else {
                return m_transaction.getID();
            }
        }
    }

    private static GlobalizedMessage gz(final String key) {
        return GlobalizationUtil.globalize(key);
    }

    private static String lz(final String key) {
        return (String) gz(key).localize();
    }
}
