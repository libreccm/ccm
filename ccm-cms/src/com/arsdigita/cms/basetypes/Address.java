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
package com.arsdigita.cms.basetypes;


import com.arsdigita.globalization.LocaleNegotiator;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.TreeMap;

/**
 * <p><code>DomainObject</code> class to represent address <code>ContentType</code>
 * objects.
 * <br />
 * This content type represents a generic address which is not country specific.
 * It provides methods for creating new address objects, retrieving existing 
 * objects from the persistent storage and retrieving and setting is properties.</p>
 * <p>This class extends {@link com.arsdigita.cms.ContentItem content item} and
 * adds extended attributes specific for an not country specific address:</p>
 * 
 * @author Sören Bernstein
 **/
public class Address extends ContentPage {

    /** PDL property name for address */
    public static final String ADDRESS = "address";
    /** PDL property name for postal code */
    public static final String POSTAL_CODE = "postalCode";
    /** PDL property name for city */
    public static final String CITY = "city";
    /** PDL property name for state */
    public static final String STATE = "state";
    /** PDL property name for country iso code */
    public static final String ISO_COUNTRY_CODE = "isoCountryCode";

    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE
        = "com.arsdigita.cms.basetypes.Address";

    private static AddressConfig s_config = new AddressConfig();
    static {
	    s_config.load();
    }
    public static AddressConfig getConfig()
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
    public String getAddress() {
        return (String) get(ADDRESS);
    }

    public void setAddress(String address) {
        set(ADDRESS, address);
    }

    public String getIsoCountryCode() {
        return (String) get(ISO_COUNTRY_CODE);
    }

    public void setIsoCountryCode(String isoCountryCode) {
        set(ISO_COUNTRY_CODE, isoCountryCode);
    }

    public String getPostalCode() {
        return (String) get(POSTAL_CODE);
    }

    public void setPostalCode(String postalCode) {
        set(POSTAL_CODE, postalCode);
    }

    public String getCity() {
        return (String) get(CITY);
    }

    public void setCity(String city) {
        set(CITY, city);
    }

    public String getState() {
        return (String) get(STATE);
    }

    public void setState(String state) {
        set(STATE, state);
    }

    // Convert the iso country code to country names using the current locale
    public static String getCountryNameFromIsoCode(String isoCountryCode) {
     
        LocaleNegotiator negotiatedLocale = new LocaleNegotiator("", "", "", null);
        java.util.Locale locale = new java.util.Locale("", isoCountryCode);
        return locale.getDisplayCountry(negotiatedLocale.getLocale());

    }
    
    // Get a sorted list auf all countries
    public static TreeMap getSortedListOfCountries(Locale inLang) {
        
        LocaleNegotiator negotiatedLocale = new LocaleNegotiator("", "", "", null);
        String[] countries = Locale.getISOCountries();
        TreeMap <String,String> countryNames = new TreeMap<String,String>();

        for(String country : countries) {
            if(inLang != null) {
                countryNames.put(new java.util.Locale("", country).getDisplayCountry(inLang), country);
            } else {
                countryNames.put(new java.util.Locale("", country).getDisplayCountry(negotiatedLocale.getLocale()), country);
            }
        }

        return countryNames;
    }
}
