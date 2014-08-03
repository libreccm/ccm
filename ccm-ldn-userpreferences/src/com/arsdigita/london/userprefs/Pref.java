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
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;

import java.sql.SQLException;

import org.apache.log4j.Logger;

public class Pref extends DomainObject {
    private static final Logger s_log = Logger.getLogger( Pref.class );

    public static final String ID = "id";
    public static final String KEY = "key";
    public static final String VALUE = "value";

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.london.userprefs.Pref";

    Pref() {
        super( BASE_DATA_OBJECT_TYPE );

        try {
            set( ID, Sequences.getNextValue() );
        } catch( SQLException ex ) {
            throw new UncheckedWrapperException( ex );
        }
    }

    public Pref( OID oid ) {
        super( oid );
    }

    public Pref( DataObject obj ) {
        super( obj );
    }

    static void domainInit() {
        DomainObjectFactory.registerInstantiator(
            BASE_DATA_OBJECT_TYPE,
            new DomainObjectInstantiator() {
                @Override
                protected DomainObject doNewInstance( DataObject obj ) {
                    return new Pref( obj );
                }
            }
        );
    }

    public String getKey() {
        return get( KEY ).toString();
    }

    void setKey( String key ) {
        set( KEY, key );
    }

    public String getValue() {
        return get( VALUE ).toString();
    }

    void setValue( String value ) {
        set( VALUE, value );
    }
}
