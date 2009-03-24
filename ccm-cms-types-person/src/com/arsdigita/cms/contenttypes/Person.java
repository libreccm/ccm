/*
 * Copyright (C) 2009 University Bremen, Center for Social Politics, Parkallee 39, 28209 Bremen.
 *
 */

package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

/**
 * Basic Person Contenttype for OpenCCM.
 *
 * @author Jens Pelzetter
 */
public class Person extends ContentItem {

    public static final String SURNAME = "surname";
    public static final String GIVENNAME = "givenname";
    public static final String TITLEPRE = "titlepre";
    public static final String TITLEPOST = "titlepost";
    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.Person";
    private static final PersonConfig s_config = new PersonConfig();


    static {
        s_config.load();
    }

    public static final PersonConfig getConfig() {
        return s_config;
    }

    /**
     * Default constructor. This creates a new (empty) Person.
     **/
    public Person() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public Person(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Person(OID id) throws DataObjectNotFoundException {
        super(id);
    }

    public Person(DataObject obj) {
        super(obj);
    }

    public Person(String type) {
        super(type);
    }

    public void beforeSave() {
        super.beforeSave();

        Assert.exists(getContentType(), ContentType.class);
    }

     /* accessors *****************************************************/
     public String getSurname() {
         return (String)get(SURNAME);
     }
     public void setSurname(String surname) {
         set(SURNAME, surname);
     }

     public String getGivenName() {
         return (String)get(GIVENNAME);
     }
     public void setGivenName(String givenName) {
         set(GIVENNAME, givenName);
     }

     public String getTitlePre() {
         return (String)get(TITLEPRE);
     }
     public void setTitlePre(String titlePre) {
         set(TITLEPRE, titlePre);
     }

     public String getTitlePost() {
         return (String)get(TITLEPOST);
     }
     public void setTitlePost(String titlePost) {
         set(TITLEPOST, titlePost);
     }
}
