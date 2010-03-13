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

import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.PersonName;
import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.util.Assert;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class UserCSVEntry {
    private static final Logger s_log =
        Logger.getLogger( UserCSVEntry.class );

    public static final String[] s_fields =
    { "First Name", "Last name", "E-mail address",
      "Primary Windows NT Account" };
    
    private static final int FIRST_NAME                = 0;
    private static final int LAST_NAME                = 1;
    private static final int EMAIL_ADDRESS        = 2;
    private static final int NT_ACCOUNT                = 3;

    private static final int EOF        = -1;
    private static final int QUOTE        = 34;
    private static final int COMMA        = 44;
    private static final int CR                = 13;
    private static final int LF                = 10;

    private ArrayList m_values = new ArrayList( s_fields.length );
    private String m_email = null;

    private static Reader s_csv;
    private static StringBuffer s_buffer;
    private static int s_char;

    private UserCSVEntry() { }

    public static void init( Reader r ) {
        s_csv = r;
        s_buffer = new StringBuffer();
        nextChar();

    }

    public static void skipEntry() {
        try {
            do {
                s_char = s_csv.read();
                System.err.print(new String(new byte[] { (byte)s_char}));
            } while( s_char != CR && s_char != LF && s_char != EOF );
        } catch( IOException ex ) {
            throw new UncheckedWrapperException( ex );
        }
    }

    public static synchronized boolean hasMore() {
        return s_csv != null;
    }

    public static synchronized UserCSVEntry nextEntry() {
        Assert.isTrue(hasMore(), "has more entries");

        UserCSVEntry entry = new UserCSVEntry();
        s_log.debug("Starting entry");
        do {
            entry.m_values.clear();
            s_buffer.setLength( 0 );

            boolean inQuotes = false;
            do {
                if( s_char == EOF || s_char == CR || s_char == LF ) {
                    addField( entry.m_values );
                    s_log.debug("Got one " + entry.m_values);

                    if( s_char != EOF ) {
                        do {
                            nextChar();
                        } while( s_char == LF || s_char == CR );
                    }

                    break;
                }

                if ( s_char == QUOTE ) {
                    inQuotes = !inQuotes;
                } else if( !inQuotes && s_char == COMMA ) {
                    addField( entry.m_values );
                    s_log.debug("Got one more " + entry.m_values);
                } else {
                    s_buffer.append( (char) s_char );
                }

                nextChar();
            } while( true );
        } while( s_char != EOF && entry.m_values.size() != s_fields.length );

        if ( s_char == EOF ) {
            // Clean up handles for garbage collection before terminating
            try {
                s_csv.close();
            } catch( IOException ex ) {
                throw new UncheckedWrapperException( ex );
            }
            s_csv = null;
            s_buffer = null;
        }
        
        return (entry.m_values.size() == s_fields.length ? entry : null);
    }

    private static void nextChar() {
        try {
            s_char = s_csv.read();
        } catch( IOException ex ) {
            throw new UncheckedWrapperException( ex );
        }
    }

    private static void addField( ArrayList l ) {
        l.add( s_buffer.toString().trim() );
        s_buffer.setLength( 0 );
    }

    public String getFirstName() {
        return m_values.get( FIRST_NAME ).toString();
    }

    public String getLastName() {
        return m_values.get( LAST_NAME ).toString();
    }

    public String getNTAccount() {
        return m_values.get( NT_ACCOUNT ).toString();
    }

    public String getPrimaryEmail() {
        return m_values.get( EMAIL_ADDRESS ).toString();
    }

    public boolean isValid() {
        s_log.debug("Checking " + getFirstName() + ":" + getLastName() + ":" + getNTAccount() + ":" + getPrimaryEmail());
        // Valid NT account
        String nt = getNTAccount();
        nt = nt.toLowerCase();

        if ( nt.length() == 0 ) { 
            s_log.debug("NT account empty");
            return false;
        }

        int sepInd = nt.indexOf( "\\" );

        // No domain\\user seperator
        if ( sepInd == -1 ) {
            s_log.debug("NT account missing separator");
            return false;
        }

        String userName = nt.substring( sepInd + 1 );

        // User part doesn't exist
        if ( userName.length() == 0 ) {
            s_log.debug("NT account missing username");
            return false;
        }

        // User is an administrator
        if ( userName.equals( "administrator" ) ) {
            s_log.debug("NT account is administrator");
            return false;
        }


        // User has first and last names
        if ( getFirstName().length() == 0 || getLastName().length() == 0 ) {
            s_log.debug("First / last name empty");
            return false;
        }
        
        return true;
    }

    public void createUser() {
        String firstName = getFirstName();
        String lastName = getLastName();
        String ident = getNTAccount();
        String email = getPrimaryEmail();
        
        User user = new User();

        PersonName name = user.getPersonName();
        name.setGivenName( firstName );
        name.setFamilyName( lastName );

        user.setPrimaryEmail( new EmailAddress( email ) );
        
        UserLogin login = UserLogin.create(user, ident);
    }

    private boolean empty( String s ) {
        return s == null || s.length() == 0;
    }

    public String toString() {
        return getPrimaryEmail() + ":" +
            getNTAccount();
    }
}
