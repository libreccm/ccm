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

import com.arsdigita.auth.http.UserLogin;
import com.arsdigita.auth.http.UserLogin;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.EmailParameter;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.PersonName;
import com.arsdigita.kernel.UserAuthentication;
import com.arsdigita.kernel.User;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;

/**
 * Form used to add a new user to the system.
 *
 * @author matthew@arsdigita.com
 * @version $Id: UserForm.java 759 2005-09-02 15:25:32Z sskracic $
 */

class UserForm extends Form
    implements FormProcessListener,
               FormInitListener
{
    private static final Logger s_log =
        Logger.getLogger( UserForm.class );

    protected final String FIRST_NAME = "firstName";
    protected final String LAST_NAME = "lastName";
    protected final String EMAIL = "email";
    protected final String NT_USER = "ntUser";

    protected TextField m_firstName;
    protected TextField m_lastName;
    protected TextField m_email;
    protected TextField m_ntUser;
    protected Submit m_save;

    protected RequestLocal m_user;

    public UserForm( RequestLocal user ) {
        super( "userForm" );
        
        m_user = user;

        m_firstName = new TextField( FIRST_NAME );
        m_lastName = new TextField( LAST_NAME );
        m_email = new TextField( new EmailParameter( EMAIL ) );
        m_ntUser = new TextField( NT_USER );

        m_save = new Submit( "Save" );

        NotEmptyValidationListener notEmpty =
            new NotEmptyValidationListener();

        m_firstName.addValidationListener( notEmpty );
        m_lastName.addValidationListener( notEmpty );
        m_email.addValidationListener( notEmpty );
        m_ntUser.addValidationListener( notEmpty );

        add( new Label( "First Name" ) );
        add( m_firstName );
        add( new Label( "Last Name" ) );
        add( m_lastName );
        add( new Label( "Email address" ) );
        add( m_email );
        add( new Label( "Windows NT user account" ) );
        add( m_ntUser );

        add( m_save );

        addProcessListener( this );
        addInitListener( this );
    }

    public void init( FormSectionEvent e ) {
        PageState ps = e.getPageState();
        FormData data = e.getFormData();

        User user = (User) m_user.get( ps );
        if( user == null ) return;

        UserLogin login = UserLogin.findByUser(user);

        PersonName name = user.getPersonName();
        data.put( FIRST_NAME, name.getGivenName() );
        data.put( LAST_NAME, name.getFamilyName() );
        data.put( EMAIL, user.getPrimaryEmail() );
        data.put( NT_USER, login == null ? null : login.getLogin());
    }

    public void process( FormSectionEvent e ) 
        throws FormProcessException 
    {
        PageState ps = e.getPageState();
        FormData  data  = e.getFormData();

        boolean newUser = false;

        User user = (User) m_user.get( ps );
        if ( user == null ) {
            user = new User();
            newUser = true;
        }
        UserLogin login = UserLogin.findByUser(user);

        EmailAddress email =
            new EmailAddress( 
                ( (InternetAddress) m_email.getValue( ps ) )
                .getAddress() );

        s_log.debug( "email = " + user.getPrimaryEmail() );
        s_log.debug( "new email = " + email );

        if( user.getPrimaryEmail() == null ||
            !user.getPrimaryEmail().equals( email ) )
            user.setPrimaryEmail( email );

        PersonName name = user.getPersonName();
        name.setGivenName( m_firstName.getValue( ps ).toString() );
        name.setFamilyName( m_lastName.getValue( ps ).toString() );
        
        String ident = m_ntUser.getValue( ps ).toString();
        if (login == null) {
            login = UserLogin.create(user, ident);
        } else {
            login.setLogin(ident);
        }
            
        // We don't need any user credentials, but include them
        // because ACS expects them

        if( newUser ) {
            UserAuthentication auth =
                UserAuthentication.createForUser(user);

            auth.setPassword( "camden" );
            auth.setPasswordQuestion( "camden" );
            auth.setPasswordAnswer( "camden" );
            auth.save();
        }

        // Blank the form
        data.put( FIRST_NAME, null );
        data.put( LAST_NAME, null );
        data.put( EMAIL, null );
        data.put( NT_USER, null );
    }
}
