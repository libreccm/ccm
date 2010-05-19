/*
 * Copyright (C) 2009 Jens Pelzetter, for the Center of Social Politics of the University of Bremen
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

import com.arsdigita.cms.contenttypes.*;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentPage;
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
public class Person extends ContentPage {

    public static final String PERSON = "person";
    public static final String SURNAME = "surname";
    public static final String GIVENNAME = "givenname";
    public static final String TITLEPRE = "titlepre";
    public static final String TITLEPOST = "titlepost";    
    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.basetypes.Person";


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
