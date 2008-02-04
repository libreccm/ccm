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
package com.arsdigita.forum.ui.admin;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.persistence.DataQuery;

import java.math.BigDecimal;

import javax.servlet.ServletException;

public abstract class MembersDisplay extends SimpleContainer
{
    private MembersDisplay m_membersDisplay;

    private List m_users = new List() {
        public void respond( PageState ps ) throws ServletException {
            super.respond( ps );

            BigDecimal userID = new BigDecimal( ps.getControlEventValue() );
            removeUser( ps, userID );
        }
    };

    public MembersDisplay() {
        super();

        m_users.setModelBuilder( new UserListModelBuilder() );
        m_users.setCellRenderer( new UserListCellRenderer() );
        m_users.setEmptyView(new Label("There are no assigned users / groups"));
        add( m_users );

        m_membersDisplay = this;
    }

    public void setClassAttr(String value) {
        m_users.setClassAttr(value);
    }

    public void setIdAttr(String value) {
        m_users.setIdAttr(value);
    }

    private class UserListModelBuilder implements ListModelBuilder {
        private boolean m_locked = false;

        public boolean isLocked() {
            return m_locked;
        }

        public void lock() {
            m_locked = true;
        }

        public ListModel makeModel( List l, PageState ps ) {
            final DataQuery members = getUsers( ps );
            return new ListModel() {
                public Object getElement() {
                    return m_membersDisplay.getDisplayName( members );
                }

                public String getKey() {
                    return m_membersDisplay.getKey( members );
                }

                public boolean next() {
                    return members.next();
                }
            };
        }
    }

    private class UserListCellRenderer implements ListCellRenderer {
        public Component getComponent( List list, PageState state, Object value,
                                       String key, int index,
                                       boolean isSelected ) {
            SimpleContainer c = new SimpleContainer();
            c.add( new Label( value.toString() + " [" ) );
            c.add( new ControlLink( "delete" ) );
            c.add( new Label( "]" ) );

            return c;
        }
    }

    protected abstract DataQuery getUsers( PageState ps );

    protected abstract String getDisplayName( DataQuery q );

    protected abstract String getKey( DataQuery q );

    protected abstract void removeUser( PageState ps, BigDecimal userID );
}
