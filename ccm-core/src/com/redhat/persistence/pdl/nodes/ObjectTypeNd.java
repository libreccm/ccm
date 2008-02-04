/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.redhat.persistence.pdl.nodes;

/**
 * ObjectType
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/08/16 $
 **/

public class ObjectTypeNd extends Node {

    public final static String versionId = "$Id: ObjectTypeNd.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public static final Field NAME =
        new Field(ObjectTypeNd.class, "name", IdentifierNd.class, 1, 1);
    public static final Field EXTENDS =
        new Field(ObjectTypeNd.class, "extends", TypeNd.class, 0, 1);
    public static final Field CLASS =
        new Field(ObjectTypeNd.class, "class", JavaClassNd.class, 0, 1);
    public static final Field ADAPTER =
        new Field(ObjectTypeNd.class, "adapter", JavaClassNd.class, 0, 1);
    public static final Field PROPERTIES =
        new Field(ObjectTypeNd.class, "properties", PropertyNd.class);
    public static final Field OBJECT_KEY =
        new Field(ObjectTypeNd.class, "objectKey", ObjectKeyNd.class, 0, 1);
    public static final Field REFERENCE_KEY =
        new Field(ObjectTypeNd.class, "referenceKey", ReferenceKeyNd.class, 0,
                  1);
    public static final Field UNIQUE_KEYS =
        new Field(ObjectTypeNd.class, "uniqueKeys", UniqueKeyNd.class);
    public static final Field AGGRESSIVE_LOAD =
        new Field(ObjectTypeNd.class, "aggressiveLoad",
                  AggressiveLoadNd.class, 0, 1);
    public static final Field JOIN_PATHS =
        new Field(ObjectTypeNd.class, "joinPaths", JoinPathNd.class);
    public static final Field EVENTS =
        new Field(ObjectTypeNd.class, "events", EventNd.class);


    private boolean m_isVersioned;

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onObjectType(this);
    }

    public String getQualifiedName() {
        return getFile().getModel().getName() + "." + getName().getName();
    }

    public IdentifierNd getName() {
        return (IdentifierNd) get(NAME);
    }

    public void setVersioned() {
        m_isVersioned = true;
    }

    public boolean isVersioned() {
        return m_isVersioned;
    }

    public TypeNd getExtends() {
        return (TypeNd) get(EXTENDS);
    }

    public ReferenceKeyNd getReferenceKey() {
        return (ReferenceKeyNd) get(REFERENCE_KEY);
    }

    public ObjectKeyNd getObjectKey() {
        return (ObjectKeyNd) get(OBJECT_KEY);
    }

    private boolean m_returns = false;

    public boolean hasReturns() {
        return m_returns;
    }

    public void setReturns(boolean value) {
        m_returns = value;
    }

    public JavaClassNd getJavaClass() {
	return (JavaClassNd) get(CLASS);
    }

    public JavaClassNd getAdapterClass() {
	return (JavaClassNd) get(ADAPTER);
    }

}
