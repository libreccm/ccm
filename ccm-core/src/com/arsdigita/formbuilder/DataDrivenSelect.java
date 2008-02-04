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
package com.arsdigita.formbuilder;

import com.arsdigita.formbuilder.PersistentWidget;

import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.ObjectType;

import java.math.BigDecimal;
import com.arsdigita.bebop.Component;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.Session;
import com.arsdigita.bebop.form.Select;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.bebop.form.MultipleSelect;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.persistence.DataQuery;


public class DataDrivenSelect extends PersistentWidget {
    // First of all a whole load of constants for the PDL

    public static final String BASE_DATA_OBJECT_TYPE
        = "com.arsdigita.formbuilder.DataDrivenSelect";

    public static final String MULTIPLE = "multiple";
    public static final String QUERY = "queryId";


    // Then, the six standard constructors

    public DataDrivenSelect() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public DataDrivenSelect(String typeName) {
        super(typeName);
    }

    public DataDrivenSelect(ObjectType type) {
        super(type);
    }

    public DataDrivenSelect(DataObject obj) {
        super(obj);
    }

    public DataDrivenSelect(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public DataDrivenSelect(OID oid)
        throws DataObjectNotFoundException {

        super(oid);
    }

    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }



    // Now the attribute accessors

    public boolean isMultiple() {
        return ((Boolean)get(MULTIPLE)).booleanValue();
    }

    public void setMultiple(boolean multiple) {
        set(MULTIPLE, new Boolean(multiple));
    }

    public PersistentDataQuery getQuery()
        throws DataObjectNotFoundException {
        return new PersistentDataQuery((BigDecimal)get(QUERY));
    }

    public void setQuery(PersistentDataQuery query) {
        set(QUERY, query.getID());
    }

    public BigDecimal getQueryID() {
        return (BigDecimal)get(QUERY);
    }

    public void setQueryID(BigDecimal query) {
        set(QUERY, query);
    }


    // Finally put your custom code here

    public Component createComponent() {
        Select select = (isMultiple() ?
                         (Select)new MultipleSelect(getParameterName()) :
                         (Select)new SingleSelect(getParameterName()));
        PersistentDataQuery query = null;
        try {
            query = getQuery();
        } catch (DataObjectNotFoundException ex) {
            throw new UncheckedWrapperException(ex);
        }

        String name = query.getName();

        Session ssn = SessionManager.getSession();
        DataQuery items = ssn.retrieveQuery(name);

        while (items.next()) {
            String id = items.get("id").toString();
            String label = items.get("label").toString();

            select.addOption(new Option(id, label));
        }

        return select;
    }
}
