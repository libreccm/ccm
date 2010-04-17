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
package com.arsdigita.cms.ui.role;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.PartyCollection;
import com.arsdigita.kernel.Role;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.DataQueryDataCollectionAdapter;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import org.apache.log4j.Logger;

/**
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: AdminTableModelBuilder.java 287 2005-02-22 00:29:02Z sskracic $
 */
class AdminTableModelBuilder extends AbstractTableModelBuilder {

    private static final Logger s_log = Logger.getLogger
        (AdminTableModelBuilder.class);

    private final RoleRequestLocal m_role;

    AdminTableModelBuilder(final RoleRequestLocal role) {
        m_role = role;
    }

    public final TableModel makeModel(final Table table,
                                      final PageState state) {
        final Role role = m_role.getRole(state);

        Session session = SessionManager.getSession();
        DataQuery query = session.retrieveQuery
            ("com.arsdigita.cms.roleAdminListing");
        
        query.setParameter("roleID", role.getID());

        final DataCollection admins = new DataQueryDataCollectionAdapter
            (query, "party");

        return new Model(new PartyCollection(admins));
    }

    private static class Model implements TableModel {
        private Party m_party;
        private final PartyCollection m_parties;

        Model(final PartyCollection parties) {
            m_parties = parties;
        }
        
        public final int getColumnCount() {
            return 3;
        }

        public final boolean nextRow() {
            if (m_parties.next()) {
                m_party = m_parties.getParty();

                return true;
            } else {
                m_parties.close();
                
                return false;
            }
        }

        public final Object getKeyAt(final int column) {
            return m_party.getID();
        }

        public final Object getElementAt(final int column) {
            switch (column) {
            case 0:
                return m_party.getDisplayName();
            case 1:
                final EmailAddress email = m_party.getPrimaryEmail();

                if (email == null) {
                    return lz("cms.ui.none");
                } else {
                    return email.toString();
                }
            case 2:
                return lz("cms.ui.role.admin.remove");
            default:
                throw new IllegalStateException();
            }
        }
    }

    protected final static String lz(final String key) {
        return (String) GlobalizationUtil.globalize(key).localize();
    }
}
