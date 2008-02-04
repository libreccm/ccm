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

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.PaginationModelBuilder;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.persistence.DataQuery;

import java.math.BigDecimal;


public abstract class UserPicker extends FormSection
    implements FormProcessListener, FormInitListener
{
    private static final int USER_LIST_SIZE = 10;

    private static final String ADD_USER = "addUser";

    private StringParameter m_nameParam;

    private TextField m_name;
    private List m_userList;
    private Paginator m_pg;

    public UserPicker() {
        addWidgets();
        addDisplay();
        
        addProcessListener( this );
        addInitListener( this );
    }

    protected void addWidgets() {
        m_nameParam = new StringParameter("name");

        BoxPanel attrs = new BoxPanel( BoxPanel.HORIZONTAL );

        Label nameLab = new Label( "Name" );
        m_name = new TextField("name1");

        attrs.add( nameLab );
        attrs.add( m_name );
        attrs.add( new Submit( "search", "Search" ) );

        add( attrs );
    }

    protected void addDisplay() {
        BoxPanel attrs = new BoxPanel( BoxPanel.VERTICAL );

        m_userList = new List();
        m_userList.setModelBuilder( new UserListModelBuilder() );
        m_userList.setCellRenderer( new UserListCellRenderer() );
        m_pg = new Paginator( (PaginationModelBuilder)
                              m_userList.getModelBuilder(), USER_LIST_SIZE );
        m_userList.addActionListener(new UserActionListener());
        
        attrs.add( m_pg );
        attrs.add( m_userList );
        add(attrs);
    }

    private class UserListModelBuilder
        implements ListModelBuilder, PaginationModelBuilder
    {
        private boolean m_locked = false;
        private RequestLocal m_query = new RequestLocal();

        public boolean isLocked() {
            return m_locked;
        }

        public void lock() {
            m_locked = true;
        }

        public boolean isVisible( PageState ps ) {
            return m_userList.isVisible( ps );
        }

        public int getTotalSize( Paginator pg, PageState ps ) {
            DataQuery query = getDataQuery( ps );

            if ( query == null ) {
                return 0;
            }

            return (int) query.size();
        }

        private DataQuery getDataQuery( PageState state ) {
            return getUsers( state, (String)state.getValue(m_nameParam));
        }

        public ListModel makeModel( List l, PageState ps ) {
            DataQuery q = getDataQuery( ps );
            if ( q == null ) {
                return List.EMPTY_MODEL;
            }

            q.setRange( new Integer( m_pg.getFirst( ps ) ),
                        new Integer( m_pg.getLast( ps ) + 1) );
            return new UserListModel( q );
        }
    }

    private class UserListModel
        implements ListModel
    {
        private DataQuery m_query;

        public UserListModel( DataQuery query ) {
            m_query = query;
        }

        public Object getElement() {
            return getDisplayName( m_query );
        }

        public String getKey() {
            return UserPicker.this.getKey( m_query );
        }

        public boolean next() {
            return m_query.next();
        }
    }

    private class UserListCellRenderer
        implements ListCellRenderer
    {
        public Component getComponent( List l, PageState ps, Object value,
                                       String key, int index,
                                       boolean isSelected ) {
            return new ControlLink( value.toString() );
        }
    }

    private class UserActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String key = (String)m_userList.getSelectedKey(e.getPageState());
            addUser( e.getPageState(), new BigDecimal( key ) );
            setListVisible(e.getPageState(), false);
            m_pg.reset(e.getPageState());
        }
    }

    private void setListVisible( PageState ps,
                                 boolean state) {
        m_userList.setVisible( ps, state );
        m_pg.setVisible( ps, state );
    }

    public void init( FormSectionEvent e ) {
        PageState ps = e.getPageState();
        
        m_name.setValue(ps, ps.getValue(m_nameParam));
    }

    public void process( FormSectionEvent e ) {
        PageState ps = e.getPageState();
        
        ps.setValue(m_nameParam, m_name.getValue(ps));

        m_pg.reset(ps);
        setListVisible(ps, ps.getValue(m_nameParam) != null);
    }


    public void register( Page p ) {
        super.register( p );

        p.setVisibleDefault( m_userList, false );
        p.setVisibleDefault( m_pg, false );
        
        p.addGlobalStateParam(m_nameParam);
    }

    protected abstract DataQuery getUsers( PageState ps, String search );

    protected abstract String getDisplayName( DataQuery q );

    protected abstract String getKey( DataQuery q );

    protected abstract void addUser( PageState ps, BigDecimal userID );
}
