/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.auth.http.ui;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.SessionManager;
import java.math.BigDecimal;
import javax.servlet.ServletException;
import org.apache.log4j.Category;

public class EditUserPane extends SimpleContainer {
    private static final Category s_log =
        Category.getInstance( EditUserPane.class );

    private static final String DISPLAY_USER = "displayUser";

    private Component m_pane = this;

    protected BigDecimalParameter m_userID =
        new BigDecimalParameter( "userID" );

    protected Form m_searchForm;
    protected TextField m_search;
    protected List m_users;
    protected UserForm m_userForm;
    protected RequestLocal m_user = new RequestLocal() {
        protected Object initialValue( PageState ps ) {
            BigDecimal id = (BigDecimal) ps.getValue( m_userID );
            s_log.debug( "userID = " + id );

            try {
                return User.retrieve( id );
            } catch( DataObjectNotFoundException ex ) {
                s_log.warn( "No user with id " + id );
                return null;
            }
        }
    };

    public EditUserPane() {
        super();

        m_search = new TextField( "search" );

        m_searchForm =
            new Form( "searchForm",
                      new BoxPanel( BoxPanel.HORIZONTAL ) );
        m_searchForm.addProcessListener( new FormProcessListener() {
            public void process( FormSectionEvent e ) {
                setSelecting( e.getPageState() );
            }
        } );

        m_searchForm.add
            ( new Label( "Find users with names containing" ) );
        m_searchForm.add( m_search );
        m_searchForm.add( new Submit( "Find Users" ) );

        m_users = new List();
        m_users.setModelBuilder( new ListModelBuilder() {
            private boolean m_locked = false;

            public ListModel makeModel( List l, PageState ps ) {
                return new UserListModel
                    ( m_search.getValue( ps ).toString() );
            }

            public boolean isLocked() { return m_locked; }
            
            public void lock() { m_locked = true; }
        } );
        m_users.setCellRenderer( new UserListCellRenderer() );

        m_userForm = new UserForm( m_user );
        m_userForm.addProcessListener( new FormProcessListener() {
            public void process( FormSectionEvent e ) {
                setSearching( e.getPageState() );
            }
        } );

        // Add UI components
        add( m_searchForm );
        add( m_users );
        add( m_userForm );
    }

    public void respond( PageState ps ) throws ServletException {
        String event = ps.getControlEventName();
        String value = ps.getControlEventValue();

        if( DISPLAY_USER.equals( event ) ) {
            ps.setValue( m_userID, new BigDecimal( value ) );
            setEditing( ps );
        }

        super.respond( ps );
    }

    public void register( Page p ) {
        p.setVisibleDefault( m_users, false );
        p.setVisibleDefault( m_userForm, false );

        p.addGlobalStateParam( m_userID );

        super.register( p );
    }

    private void setEditing( PageState ps ) {
        m_users.setVisible( ps, false );
        m_userForm.setVisible( ps, true );
    }

    private void setSelecting( PageState ps ) {
        m_users.setVisible( ps, true );
        m_userForm.setVisible( ps, false );
    }

    private void setSearching( PageState ps ) {
        m_users.setVisible( ps, false );
        m_userForm.setVisible( ps, false );
    }

    private class UserListModel implements ListModel {
        private DataQuery m_users;

        public UserListModel( String search ) {
            m_users = SessionManager.getSession().retrieveQuery
                ( "com.arsdigita.kernel.RetrieveUsers" );
            m_users.setParameter( "excludeGroupId", "0" );

            Filter f = m_users.addFilter
                ( "searchName like :search" );
            search = "%" + search + "%";
            f.set( "search", search );
        }

        public boolean next() {
            boolean next = m_users.next();
            return next;
        }

        public Object getElement() {
            String firstName = (String) m_users.get( "firstName" );
            String lastName = (String) m_users.get( "lastName" );

            return firstName + " " + lastName;
        }

        public String getKey() {
            return m_users.get( "userID" ).toString();
        }
    }

    private class UserListCellRenderer implements ListCellRenderer {
        public Component getComponent( List list, PageState ps,
                                       Object value, String key,
                                       int index,
                                       boolean isSelected ) {
            ps.setControlEvent( m_pane, DISPLAY_USER, key );
            return new ControlLink( value.toString() );
        }
    }
}
