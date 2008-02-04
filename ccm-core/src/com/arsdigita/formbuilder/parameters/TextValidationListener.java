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
package com.arsdigita.formbuilder.parameters;

import com.arsdigita.formbuilder.parameters.PersistentParameterListener;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.bebop.event.ParameterListener;
import java.math.BigDecimal;
import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.util.Assert;

import com.arsdigita.bebop.parameters.EmailValidationListener;
import com.arsdigita.bebop.parameters.WordValidationListener;
import com.arsdigita.bebop.parameters.URLValidationListener;
import com.arsdigita.bebop.parameters.SingleLineValidationListener;
import com.arsdigita.bebop.parameters.FloatValidationListener;
import com.arsdigita.bebop.parameters.IntegerValidationListener;


public class TextValidationListener extends PersistentParameterListener {

    public static final String BASE_DATA_OBJECT_TYPE =
        PersistentParameterListener.BASE_DATA_OBJECT_TYPE;

    public static final Integer TYPE_WORD = new Integer(1);
    public static final Integer TYPE_SINGLE_LINE = new Integer(2);
    public static final Integer TYPE_EMAIL = new Integer(3);
    public static final Integer TYPE_INTEGER = new Integer(4);
    public static final Integer TYPE_FLOAT = new Integer(5);
    public static final Integer TYPE_URL = new Integer(6);


    public TextValidationListener(BigDecimal id)
        throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public TextValidationListener(OID oid)
        throws DataObjectNotFoundException {
        super(oid);
    }

    public TextValidationListener(String objectType) {
        super();

        Assert.assertEquals(objectType,
                            PersistentParameterListener.BASE_DATA_OBJECT_TYPE);
    }

    public TextValidationListener(DataObject obj) {
        super(obj);
    }

    public TextValidationListener(Integer type) {
        super(typeToClassName(type));

        createListener();
    }

    // XXX hack to get around some wierd issues
    // with mdsql associations where the object
    // type in question is a subtype of the
    // one named in the association definition
    public boolean isContainerModified() {
        return false;
    }

    public void setValidationType(Integer type) {
        setClassName(typeToClassName(type));
    }

    public Integer getValidationType() {
        return classNameToType(getClassName());
    }

    public static Integer classNameToType(String className) {
        if (className.equals(WordValidationListener.class.getName())) {
            return TYPE_WORD;
        } else if (className.equals(SingleLineValidationListener.class.getName())) {
            return TYPE_SINGLE_LINE;
        } else if (className.equals(EmailValidationListener.class.getName())) {
            return TYPE_EMAIL;
        } else if (className.equals(IntegerValidationListener.class.getName())) {
            return TYPE_INTEGER;
        } else if (className.equals(FloatValidationListener.class.getName())) {
            return TYPE_FLOAT;
        } else if (className.equals(URLValidationListener.class.getName())) {
            return TYPE_URL;
        }
        return null;
    }

    public static String typeToClassName(Integer type) {
        if (type.equals(TYPE_WORD)) {
            return WordValidationListener.class.getName();
        } else if (type.equals(TYPE_SINGLE_LINE)) {
            return SingleLineValidationListener.class.getName();
        } else if (type.equals(TYPE_EMAIL)) {
            return EmailValidationListener.class.getName();
        } else if (type.equals(TYPE_INTEGER)) {
            return IntegerValidationListener.class.getName();
        } else if (type.equals(TYPE_FLOAT)) {
            return FloatValidationListener.class.getName();
        } else if (type.equals(TYPE_URL)) {
            return URLValidationListener.class.getName();
        }
        return null;
    }

    public static Integer[] getValidationTypes() {
        return new Integer[] {
            TYPE_WORD,
            TYPE_SINGLE_LINE,
            TYPE_EMAIL,
            TYPE_INTEGER,
            TYPE_FLOAT,
            TYPE_URL
        };
    }

    public static String getValidationTypeName(Integer type) {
        if (type.equals(TYPE_WORD)) {
            return "Single word";
        } else if (type.equals(TYPE_SINGLE_LINE)) {
            return "Single line";
        } else if (type.equals(TYPE_EMAIL)) {
            return "E-mail address";
        } else if (type.equals(TYPE_INTEGER)) {
            return "Whole number";
        } else if (type.equals(TYPE_FLOAT)) {
            return "Fractional number";
        } else if (type.equals(TYPE_URL)) {
            return "URL";
        }
        return null;
    }

    public ParameterListener createListener() {
        return super.createListener();
    }

}
