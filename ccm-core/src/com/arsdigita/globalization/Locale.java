/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.globalization;

import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * <p>
 * Locale DomainObject implements a persistent Locale object.
 * </p>
 *
 * @author Yon Feldman
 * @version $Revision: #12 $ $Date: 2004/08/16 $
 */
public class Locale extends DomainObject {
    public final static String versionId = "$Id: Locale.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_cat =
        Logger.getLogger(Locale.class.getName());

    public final static String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.globalization.Locale";

    /**
     * <p>
     * Constructor. Creates an instance for a new Locale.
     * </p>
     *
     * @return Locale DomainObject.
     */
    public Locale() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * <p>
     * Constructor. Creates a persistent Locale DomainObject.
     * </p>
     *
     * @param language The language of the Locale to create.
     */
    public Locale(String language) {
        this();
        setLanguage(language);
    }

    /**
     * <p>
     * Constructor. Creates a persistent Locale DomainObject.
     * </p>
     *
     * @param language The language of the Locale to create.
     * @param country The country of the Locale to create.
     *
     */
    public Locale(String language, String country) {
        this();
        setLanguage(language);
        setCountry(country);
    }

    /**
     * <p>
     * Constructor. Creates a persistent Locale DomainObject.
     * </p>
     *
     * @param language The language of the Locale to create.
     * @param country The country of the Locale to create.
     * @param variant The variant of the Locale to create.
     *
     */
    public Locale(String language, String country, String variant) {
        this();
        setLanguage(language);
        setCountry(country);
        setVariant(variant);
    }

    /**
     * <p>
     * Constructor. Retrieves a persistent Locale DomainObject.
     * </p>
     *
     * @param oid Object ID of the Locale to retrieve.
     *
     * @exception DataObjectNotFoundException Thrown if we cannot retrieve a
     *            DataObject for the specified OID.
     */
    public Locale(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    protected void initialize() {
        super.initialize();
        try {
            if (isNew() && getID() == null) {
                setID(Sequences.getNextValue());
            }
        } catch (java.sql.SQLException e) {
            throw new UncheckedWrapperException(e);
        }
    }

    /**
     * <p>
     * Constructor. Retrieves a persistent Locale DomainObject.
     * </p>
     *
     * @param dataObject DataObject of the Locale to retrieve.
     *
     */
    public Locale(DataObject dataObject) {
        super(dataObject);
    }

    /**
     * <p>
     * Fallback to the more generic version of this locale. That is, if the
     * current locale is "en_US_WIN" then this method returns "en_US", if
     * called again on that locale it will return "en", and if called again it
     * will return null.
     * </p>
     *
     * @return Locale DomainObject or null
     */
    public Locale fallback() {
        Locale locale = null;

        if (getVariant() != null && getVariant().length() > 0) {
            try {
                locale = retrieve(getLanguage(), getCountry());
            } catch (DataObjectNotFoundException e) {
                try {
                    locale = retrieve(getLanguage());
                } catch (DataObjectNotFoundException e2) {
                    locale = null;
                }
            }
        } else if (getCountry() != null && getCountry().length() > 0) {
            try {
                locale = retrieve(getLanguage());
            } catch (DataObjectNotFoundException e) {
                locale = null;
            }
        }

        return locale;
    }

    /**
     * <p>
     * Fallback to the more generic version of the locale. That is, if the
     * passed in locale is "en_US_WIN" then this method returns "en_US", if
     * called again on that locale it will return "en", and if called again it
     * will return null.
     * </p>
     *
     * @param locale java.util.Locale
     *
     * @return Locale DomainObject or null
     */
    public static Locale fallback(java.util.Locale locale) {
        Locale localeObject = null;

        if (locale != null) {
            String variant = locale.getVariant();
            String country = locale.getCountry();
            if (variant != null && variant.length() > 0) {
                try {
                    localeObject = retrieve(
                                            locale.getLanguage(),
                                            locale.getCountry()
                                            );
                } catch (DataObjectNotFoundException e) {
                    try {
                        localeObject = retrieve(locale.getLanguage());
                    } catch (DataObjectNotFoundException e2) {
                        localeObject = null;
                    }
                }
            } else if (country != null && country.length() > 0) {
                try {
                    localeObject = retrieve(locale.getLanguage());
                } catch (DataObjectNotFoundException e) {
                    localeObject = null;
                }
            }
        }

        return localeObject;
    }

    /**
     * <p>
     * Returns the appropriate object type for a Locale so that the proper
     * type validation can take place when retrieving Locales by OID.
     * </p>
     *
     * @return String The fully qualified name of the base data object type
     *         for the Locale DataObject.
     */
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * <p>
     * Return ID.
     * </p>
     *
     * @return BigDecimal The ID of this DataObject.
     */
    public BigDecimal getID() {
        return (BigDecimal) get("id");
    }

    /**
     * <p>
     * Set the ID.
     * </p>
     *
     * @param id ID.
     */
    private void setID(BigDecimal id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be empty.");
        }

        if (isNew()) {
            set("id", id);
        }
    }

    /**
     * <p>
     * Retrieve the language for this Locale.
     * </p>
     *
     * @return String ISO-639 language code.
     */
    public String getLanguage() {
        String language = (String) get("language");

        if (language == null) {
            language = "";
        }

        return language;
    }

    /**
     * <p>
     * Set the language for this Locale.
     * </p>
     *
     * @param language ISO-639 language code.
     */
    public void setLanguage(String language) {
        if (language == null || language.length() == 0) {
            throw new IllegalArgumentException("language cannot be empty.");
        }
        set("language", language);
    }

    /**
     * <p>
     * Retrieve the country for this Locale.
     * </p>
     *
     * @return String ISO-3166 country code.
     */
    public String getCountry() {
        String country = (String) get("country");

        if (country == null) {
            country = "";
        }

        return country;
    }

    /**
     * <p>
     * Set the country for this Locale.
     * </p>
     *
     * @param country ISO-3166 country code.
     */
    public void setCountry(String country) {
        set("country", country);
    }

    /**
     * <p>
     * Retrieve the variant for this Locale.
     * </p>
     *
     * @return String variant.
     */
    public String getVariant() {
        String variant = (String) get("variant");

        if (variant == null) {
            variant = "";
        }

        return variant;
    }

    /**
     * <p>
     * Set the variant for this Locale.
     * </p>
     *
     * @param variant variant.
     */
    public void setVariant(String variant) {
        set("variant", variant);
    }

    /**
     * <p>
     * Retrieve the default character set for this Locale.
     * </p>
     *
     * @return Charset DomainObject
     */
    public Charset getDefaultCharset() {
        DataObject charset = (DataObject) get("defaultCharset");
        Charset charsetObject = null;

        if (charset != null) {
            charsetObject = new Charset(charset);
        }

        return charsetObject;
    }

    /**
     * <p>
     * Set the default character set for this Locale.
     * </p>
     *
     * @param charset Charset DomainObject.
     */
    public void setDefaultCharset(Charset charset) {
        setAssociation("defaultCharset", charset);
    }

    private static DataObject load(
                                   String language,
                                   String country,
                                   String variant
                                   ) throws DataObjectNotFoundException {

        if (language == null || country == null || variant == null) {
            throw new NullPointerException("No null arguemnts allowed.");
        }

        if (s_cat.isDebugEnabled()) {
            s_cat.debug(
                        "Attempting to load Locale for: " +
                        " language = " + language +
                        ", country = " + country +
                        ", variant = " + variant
                        );
        }

        Session s = SessionManager.getSession();
        DataCollection locales = s.retrieve(BASE_DATA_OBJECT_TYPE);
        DataObject locale = null;

        // we have to check the length because we want the empty
        // string and the null value to be treated the same.
        if (language.length() > 0) {
            locales.addEqualsFilter("language", language);
        } else {
            locales.addEqualsFilter("language", null);
        }

        if (country.length() > 0) {
            locales.addEqualsFilter("country", country);
        } else {
            locales.addEqualsFilter("country", null);
        }

        if (variant.length() > 0) {
            locales.addEqualsFilter("variant", variant);
        } else {
            locales.addEqualsFilter("variant", null);
        }

        if (locales.next()) {
            locale = locales.getDataObject();
            locales.close();
            return locale;
        } else {
            throw new DataObjectNotFoundException(
                                                  "Didn't find a corresponding Locale DomainObject"
                                                  );
        }
    }

    /**
     * <p>
     * Retrieve a Locale DomainObject.
     * </p>
     *
     * @param language The language of the Locale to retrieve.
     *
     * @return Locale DomainObject
     */
    public static Locale retrieve(
                                  String language
                                  ) throws DataObjectNotFoundException {
        return retrieve(language, "", "");
    }

    /**
     * <p>
     * Retrieve a Locale DomainObject.
     * </p>
     *
     * @param language The language of the Locale to retrieve.
     * @param country The country of the Locale to retrieve.
     *
     * @return Locale DomainObject
     */
    public static Locale retrieve(
                                  String language,
                                  String country
                                  ) throws DataObjectNotFoundException {
        return retrieve(language, country, "");
    }

    /**
     * <p>
     * Retrieve a Locale DomainObject.
     * </p>
     *
     * @param language The language of the Locale to retrieve.
     * @param country The country of the Locale to retrieve.
     * @param variant The variant of the Locale to retrieve.
     *
     * @return Locale DomainObject
     */
    public static Locale retrieve(
                                  String language,
                                  String country,
                                  String variant
                                  ) throws DataObjectNotFoundException {
        return new Locale(load(language, country, variant));
    }

    /**
     * <p>
     * Retrieve a Locale DomainObject based on a java.util.Locale
     * </p>
     *
     * @param java.util.Locale locale
     *
     * @return Locale
     */
    public static Locale fromJavaLocale(java.util.Locale locale)
        throws GlobalizationException {

        Locale localeObject = null;

        if (locale != null) {
            try {
                localeObject = retrieve(
                                        locale.getLanguage(),
                                        locale.getCountry(),
                                        locale.getVariant()
                                        );
            } catch (DataObjectNotFoundException e) {
                if (s_cat.isDebugEnabled()) {
                    s_cat.debug(
                                "Locale " + locale.toString() + " is not supported."
                                );
                }
                throw new GlobalizationException(
                                                 "Locale " + locale.toString() + " is not supported."
                                                 );
            }
        }

        return localeObject;
    }

    /**
     * <p>
     * Retrieve the best matching Locale DomainObject based on a
     * java.util.Locale
     * </p>
     *
     * @param java.util.Locale locale
     *
     * @return Locale
     */
    public static Locale fromJavaLocaleBestMatch(java.util.Locale locale) {
        Locale localeObject = null;

        if (locale != null) {
            try {
                localeObject = fromJavaLocale(locale);
            } catch (GlobalizationException ge) {
                localeObject = fallback(locale);
            }
        }

        return localeObject;
    }

    /**
     * <p>
     * Create an equivalent java.util.Locale
     * </p>
     *
     * @return java.util.Locale
     */
    public java.util.Locale toJavaLocale() {
        String language = getLanguage();
        String country = getCountry();
        String variant = getVariant();

        if (country == null) {
            country = "";
        }
        if (variant == null) {
            variant = "";
        }

        return new java.util.Locale(language, country, variant);
    }

    /**
     * <p>
     * Create a java.util.Locale from a Locale DomainObject
     * </p>
     *
     * @param Locale locale DomainObject
     *
     * @return java.util.Locale
     */
    public static java.util.Locale toJavaLocale(Locale locale) {
        return locale.toJavaLocale();
    }

    protected void beforeSave() {
        // safeguard against null Charset
        if (getDefaultCharset() == null) {
            Charset c = new Charset();
            c.setCharset("dummyCharset" + c.getID());
            c.save();
            setDefaultCharset(c);
        }
        super.beforeSave();
    }

    protected void afterSave() {
        super.afterSave();
        Globalization.loadLocaleToCharsetMap();
    }

    protected void afterDelete() {
        super.afterDelete();
        Globalization.loadLocaleToCharsetMap();
    }
}
