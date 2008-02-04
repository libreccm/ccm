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
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import java.util.TooManyListenersException;

public class UserManageForm extends Form {
    private static final String FORM_NAME = "userSearchForm";
    private static final String DOMAIN_SELECT = "domainSelect";
    private static final String DOMAIN_FIELD = "domainField";
    private static final String USERNAME = "userName";

    private SingleSelect m_domainSelect = new SingleSelect( DOMAIN_SELECT );
    private TextField m_domainField = new TextField( DOMAIN_FIELD );
    private TextField m_username = new TextField( USERNAME );
    private Submit m_submit = new Submit( "Add/Edit User" );

    private ColumnPanel m_editForm = new ColumnPanel( 2 );

    public UserManageForm() {
        super( FORM_NAME, new BoxPanel( BoxPanel.VERTICAL ) );

        try {
            m_domainSelect.addPrintListener( new PrintListener() {
                public void prepare( PrintEvent ev ) {
                    PageState ps = ev.getPageState();

                    DataQuery q =
                        SessionManager.getSession().retrieveQuery
                        ( "com.arsdigita.auth.ntlm.GetDomains" );
                    while ( q.next() ) {
                        String domain = q.getParameter( "domain" ).toString();
                        m_domainSelect.addOption( new Option( domain ), ps );
                    }
                }
            } );
        } catch( TooManyListenersException e ) {
            throw new UncheckedWrapperException( e );
        }

        initEditForm();

        BoxPanel domainEntry = new BoxPanel( BoxPanel.VERTICAL );
        domainEntry.add( m_domainSelect );
        domainEntry.add( m_domainField );

        BoxPanel userSearch = new BoxPanel( BoxPanel.HORIZONTAL );

        userSearch.add( new Label( "Domain" ) );
        userSearch.add( domainEntry );
        userSearch.add( new Label( "&nbsp", false ) );
        userSearch.add( new Label( "Username" ) );
        userSearch.add( m_username );
        userSearch.add( m_submit );

        add( userSearch );

        add( m_editForm );
    }

    private void initEditForm() {
    }
}
