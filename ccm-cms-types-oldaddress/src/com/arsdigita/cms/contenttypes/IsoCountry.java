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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * This class represents country information, iso code and name.  The country
 * information is being used by {@link com.arsdigita.cms.contenttypes.Address}.
 *
 * @author <a href="mailto:dominik@redhat.com">Dominik Kacprzak</a>
 * @version $Revision: #4 $ $Date: 2004/08/17 $
 **/
public class IsoCountry extends DomainObject {        
    
    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE
        = "com.arsdigita.cms.contenttypes.IsoCountry";
    
    /** PDL property name for country iso code */
    public static final String ISO_CODE = "isoCode";
    /** PDL property name for country name */
    public static final String COUNTRY_NAME = "countryName";
    
    public IsoCountry( ) {
        super( BASE_DATA_OBJECT_TYPE );
    }
    
    public IsoCountry( OID oid ) throws DataObjectNotFoundException {
        super( oid );
    }
    
    public IsoCountry( DataObject obj ) {
        super(obj);
    }
    
    public String getIsoCode( ) {
        return ( String ) get( ISO_CODE );
    }
    
    public void setIsoCode( String isoCode ) {
        set( ISO_CODE, isoCode );
    }
    
    /** 
     *
     */
    public String getCountryName( ) {
        return ( String ) get( COUNTRY_NAME );
    }
    
    /**
     * Sets country name
     *
     * @param countryName
     */
    public void setCountryName( String countryName ) {
        set( COUNTRY_NAME, countryName );
    }

    public static DomainCollection retrieveAll() {
        DataCollection isoCodes = SessionManager.getSession()
            .retrieve( IsoCountry.BASE_DATA_OBJECT_TYPE );
        isoCodes.addOrder(COUNTRY_NAME);
        return new DomainCollection(isoCodes) {};
    }
    
    /**
     * Returns iterator of all available iso country codes as strings.
     *
     * @return iterator or <code>null</code> if there is no country codes 
     * available
     */
    public static Iterator getIsoCodes( ) {
        LinkedList list = new LinkedList();
        // get all iso codes
        DataCollection isoCodes = SessionManager.getSession()
            .retrieve( IsoCountry.BASE_DATA_OBJECT_TYPE );
        while ( isoCodes.next() ) {
            IsoCountry country = new IsoCountry( isoCodes.getDataObject( ) );
            list.addLast( country.getIsoCode() );
        }
        if ( list.size() != 0 ) {
            return list.iterator();
        }
        return null;
    }    
}
