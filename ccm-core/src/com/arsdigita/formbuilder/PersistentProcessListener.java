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
package com.arsdigita.formbuilder;

import com.arsdigita.kernel.ACSObject;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.ObjectType;

import com.arsdigita.bebop.event.FormProcessListener;

import com.arsdigita.formbuilder.util.FormBuilderUtil;

import java.math.BigDecimal;

public class PersistentProcessListener extends ACSObject {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.ProcessListener";

    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String LISTENER_CLASS = "listenerClass";
    public static final String FORM = "form";

    public PersistentProcessListener(String typeName) {
        super(typeName);
    }

    public PersistentProcessListener(ObjectType type) {
        super(type);
    }

    public PersistentProcessListener(BigDecimal id)
        throws DataObjectNotFoundException {

        this (new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public PersistentProcessListener(OID oid)
        throws DataObjectNotFoundException {

        super(oid);
    }

    public PersistentProcessListener(DataObject obj) {
        super(obj);
    }

    public static PersistentProcessListener create(String name,
                                                   String className) {
        PersistentProcessListener listener =
            new PersistentProcessListener(BASE_DATA_OBJECT_TYPE);

        listener.setName(name);
        listener.setProcessListenerClass(className);

        return listener;
    }

    protected void setup(String name,
                         String description) {
        set(NAME, name);
        set(DESCRIPTION, description);
    }

    /**
     * This default implementation will instantiate an instance of the listener
     * of the process listener class given in the constructor.
     */
    public FormProcessListener createProcessListener() {

        return (FormProcessListener)FormBuilderUtil.instantiateObject((String)get(LISTENER_CLASS));
    }

    public void setName(String name) {
        set(NAME, name);
    }

    public String getName() {
        return (String)get(NAME);
    }

    public void setProcessListenerClass(String className) {

        set(LISTENER_CLASS, className);
    }

    public String getPrcoessListenerClass() {

        return (String)get(LISTENER_CLASS);
    }

    public void setDescription(String desc) {
        set(DESCRIPTION, desc);
    }

    public String getDescription() {
        return (String)get(DESCRIPTION);
    }

    /**
     * Return the PersistentFormSection associated with this listener
     */
    public PersistentFormSection getForm() {
        DataObject obj = (DataObject) get( FORM );
        return (PersistentFormSection) DomainObjectFactory.newInstance( obj );
    }
}
