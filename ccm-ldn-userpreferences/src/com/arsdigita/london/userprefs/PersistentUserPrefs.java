/*
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1 of
the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package com.arsdigita.london.userprefs;

import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

public class PersistentUserPrefs extends DomainObject {
    private static final Logger s_log =
        Logger.getLogger( PersistentUserPrefs.class );

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.london.userprefs.PersistentUserPrefs";

    public static final String ID = "id";
    public static final String USER = "user";
    public static final String PREFS = "prefs";
    public static final String COOKIE = "cookie";

    PersistentUserPrefs() {
        super( BASE_DATA_OBJECT_TYPE );

        try {
            set( ID, Sequences.getNextValue() );
        } catch( SQLException ex ) {
            throw new UncheckedWrapperException( ex );
        }
    }

    public PersistentUserPrefs( OID oid ) {
        super( oid );
    }

    public PersistentUserPrefs( DataObject obj ) {
        super( obj );
    }

    static void domainInit() {
        DomainObjectFactory.registerInstantiator(
            BASE_DATA_OBJECT_TYPE,
            new DomainObjectInstantiator() {
                @Override
                protected DomainObject doNewInstance( DataObject obj ) {
                    return new PersistentUserPrefs( obj );
                }
            }
        );
    }

    static PersistentUserPrefs retrieveForUser( User user ) {
        DataCollection pups = SessionManager.getSession().retrieve
            ( BASE_DATA_OBJECT_TYPE );
        pups.addEqualsFilter( USER + "." + User.ID, user.getID() );

        PersistentUserPrefs prefs = null;
        if( pups.next() ) {
            prefs = new PersistentUserPrefs( pups.getDataObject() );

            if( pups.next() ) {
                s_log.warn( "User " + user.getOID() + " has multiple user " +
                            "preferences" );
            }
        }
        pups.close();

        return prefs;
    }

    static PersistentUserPrefs retrieveForCookie( Long cookie ) {
        DataCollection pups = SessionManager.getSession().retrieve
            ( BASE_DATA_OBJECT_TYPE );
        pups.addEqualsFilter( COOKIE, cookie );

        PersistentUserPrefs prefs = null;
        if( pups.next() ) {
            prefs = new PersistentUserPrefs( pups.getDataObject() );
            if( pups.next() ) {
                s_log.warn( "Cookie " + cookie + " has multiple user " +
                            "preferences" );
            }
        }
        pups.close();

        return prefs;
    }

    public String getValue( String key ) {
        DomainCollection prefs = getAllValues();
        prefs.addEqualsFilter( Pref.KEY, key );

        String value = null;
        if( prefs.next() ) {
            value = (String) prefs.get( Pref.VALUE );

            if( prefs.next() ) {
                s_log.warn( "Users prefs " + getOID() + " has multiple " +
                            "values for " + key );
            }
        }
        prefs.close();

        return value;
    }

    public DomainCollection getAllValues() {
        DataAssociation prefs = (DataAssociation) get( PREFS );
        return new DomainCollection( prefs.getDataAssociationCursor() );
    }

    public void removeValue( String key ) {
        DomainCollection prefs = getAllValues();
        prefs.addEqualsFilter( Pref.KEY, key );

        while( prefs.next() ) {
            prefs.getDomainObject().delete();
        }
    }

    public void setValue( String key, String value ) {
        DomainCollection prefs = getAllValues();
        prefs.addEqualsFilter( Pref.KEY, key );

        Pref pref;
        if( prefs.next() ) {
            pref = (Pref) prefs.getDomainObject();

            if( prefs.next() ) {
                s_log.warn( "Users prefs " + getOID() + " has multiple " +
                            "values for " + key );
            }
        } else {
            pref = new Pref();
            pref.setKey( key );
        }
        prefs.close();

        pref.setValue( value );
        if( pref.isNew() ) {
            add( PREFS, pref );
        }
    }

    public void setAllValues( Map newPrefs ) {
        if( newPrefs.isEmpty() ) {
            s_log.debug( "All values removed. Deleting prefs object." );
            delete();
            return;
        }

        s_log.debug( "Setting all values" );

        HashSet newKeys = new HashSet( newPrefs.keySet() );
        DomainCollection prefs = getAllValues();

        while( prefs.next() ) {
            String key = (String) prefs.get( Pref.KEY );
            String value = (String) prefs.get( Pref.VALUE );

            if( !newKeys.contains( key ) ) {
                prefs.getDomainObject().delete();
            }

            else {
                String newValue = (String) newPrefs.get( key );
                if( !value.equals( newValue ) ) {
                    Pref pref = (Pref) prefs.getDomainObject();
                    pref.setValue( newValue );
                }

                newKeys.remove( key );
            }
        }

        Iterator i = newKeys.iterator();
        while( i.hasNext() ) {
            String key = (String) i.next();

            Pref pref = new Pref();
            pref.setKey( key );
            pref.setValue( (String) newPrefs.get( key ) );

            add( PREFS, pref );
        }
    }

    void setUser( User user ) {
        setAssociation( USER, user );
    }

    User getUser() {
        return (User) DomainObjectFactory.newInstance
            ( (DataObject) get( USER ) );
    }

    void setCookie( Long cookie ) {
        set( COOKIE, cookie );
    }

    Long getCookie() {
        return (Long) get( COOKIE );
    }
}
