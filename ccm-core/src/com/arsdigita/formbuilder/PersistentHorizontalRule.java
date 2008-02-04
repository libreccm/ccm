/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.HorizontalLine;

import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;

import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * This class is responsible for persisting an HTML Horizontal Rule element.
 *
 * @author Matthew Booth
 *
 */
public class PersistentHorizontalRule extends PersistentComponent {

    private static final Logger s_log =
        Logger.getLogger(PersistentHorizontalRule.class);

    /**
     * The fully qualified name of the underlying DataObject of this class.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.Component";

    // *** Constructors -------------

    public PersistentHorizontalRule() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public PersistentHorizontalRule(String typeName) {
        super(typeName);
    }

    public PersistentHorizontalRule(ObjectType type) {
        super(type);
    }

    public PersistentHorizontalRule(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor that retrieves an existing object
     * from the database.
     *
     * @param id The object id of the object to retrieve
     */
    public PersistentHorizontalRule(BigDecimal id) {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * To be used by extending classes when retrieving instance from the database.
     */
    public PersistentHorizontalRule(OID oid) {
        super(oid);
    }

    public static PersistentHorizontalRule create(String textString) {
        return new PersistentHorizontalRule();
    }

    /**
     * Create the Text whose persistence is managed
     * by this domain object.
     */
    public Component createComponent() {
        return new HorizontalLine();
    }

    public boolean hasLabel() {
        return false;
    }
}
