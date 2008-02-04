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


import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

/**
 * <p><code>DomainObject</code> class to represent address <code>ContentType</code>
 * objects.
 * <br />
 * This content type represents a generic address which is not country specific.
 * It provides methods for creating new address objects, retrieving existing 
 * objects from the persistent storage and retrieving and setting is properties.</p>
 * <p>This class extends {@link com.arsdigita.cms.ContentPage content page} and
 * adds extended attributes specific for an not country specific address:</p>
 * 
 * @author <a href="mailto:dominik@redhat.com">Dominik Kacprzak</a>
 * @version $Revision: #6 $ $Date: 2004/08/17 $
 **/
public class Address extends ContentPage {

    /** PDL property name for address */
    public static final String ADDRESS = "address";
    /** PDL property name for country iso code */
    public static final String ISO_COUNTRY_CODE = "isoCountryCode";
    /** PDL property name for postal code */
    public static final String POSTAL_CODE = "postalCode";
    /** PDL property name for phone number */
    public static final String PHONE = "phone";
    /** PDL property name for mobile phone number */
    public static final String MOBILE = "mobile";
    /** PDL property name for fax number */
    public static final String FAX = "fax";
    /** PDL property name for email address */
    public static final String EMAIL = "email";
    /** PDL property name for notes */
    public static final String NOTES = "notes";
    /** PDL property name for URI*/
    public static final String URI = "uri";

    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE
        = "com.arsdigita.cms.contenttypes.Address";

    private static final AddressConfig s_config = new AddressConfig();
    static {
	    s_config.load();
    }
    public static final AddressConfig getConfig()
    {
	    return s_config;
    }

    /**
     * Default constructor. This creates a new (empty) Address.
     **/
    public Address() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i> and
     * <code>Address.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public Address(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i>.
     *
     * @param id The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public Address(OID id) throws DataObjectNotFoundException {
        super(id);
    }

    /**
     * Constructor.  Retrieves or creates a content item using the
     * <code>DataObject</code> argument.
     *
     * @param obj The <code>DataObject</code> with which to create or
     * load a content item
     */
    public Address(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor.  Creates a new content item using the given data
     * object type.  Such items are created as draft versions.
     *
     * @param type The <code>String</code> data object type of the
     * item to create
     */
    public Address(String type) {
        super(type);
    }

    /**
     * For new content items, sets the associated content type if it
     * has not been already set.
     */
    public void beforeSave() {
        super.beforeSave();
        
        Assert.exists(getContentType(), ContentType.class);
    }

    /* accessors *****************************************************/
    public String getAddress( ) {
        return ( String ) get( ADDRESS );
    }

    public void setAddress( String address ) {
        set( ADDRESS, address );
    }

    public String getCountryIsoCode( ) {
        DataObject obj = ( DataObject ) get( ISO_COUNTRY_CODE );
        if ( obj != null ) {
            IsoCountry country = new IsoCountry( obj );
            return country.getIsoCode( );
        }
        return null;
    }

    public void setCountryIsoCode( String isoCode ) {
        IsoCountry assn = null;
        try {
            OID oid = new OID( IsoCountry.BASE_DATA_OBJECT_TYPE );
            oid.set( IsoCountry.ISO_CODE, isoCode );

            assn = new IsoCountry( oid );
        } catch (DataObjectNotFoundException e) {
            assn = new IsoCountry( );
            assn.setIsoCode( isoCode );
            assn.save();
        }
        setAssociation( ISO_COUNTRY_CODE,  assn );
    }

    public String getPostalCode( ) {
        return ( String ) get( POSTAL_CODE );
    }

    public void setPostalCode( String postalCode ) {
        set( POSTAL_CODE, postalCode );
    }

    public String getPhone( ) {
        return ( String ) get( PHONE );
    }

    public void setPhone( String phone ) {
        set( PHONE, phone );
    }

    public String getMobile( ) {
        return ( String ) get( MOBILE );
    }

    public void setMobile( String mobile ) {
        set( MOBILE, mobile );
    }

    public String getFax( ) {
        return ( String ) get( FAX );
    }

    public void setFax( String fax ) {
        set( FAX, fax );
    }

    public String getEmail( ) {
        return ( String ) get( EMAIL );
    }

    public void setEmail( String email ) {
        set( EMAIL, email );
    }

    public String getNotes( ) {
        return ( String ) get( NOTES );
    }

    public void setNotes( String notes ) {
        set( NOTES, notes );
    }

    public void setURI( String uri) {
        set(URI, uri);
    }

    public String getURI() {
        return (String)get(URI);
    }

}
