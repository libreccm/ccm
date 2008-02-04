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
 * Property
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/08/16 $
 **/

public class PropertyNd extends StatementNd {

    public final static String versionId = "$Id: PropertyNd.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public static final Field TYPE =
        new Field(PropertyNd.class, "type", TypeNd.class, 1, 1);
    public static final Field NAME =
        new Field(PropertyNd.class, "name", IdentifierNd.class, 1, 1);
    public static final Field MAPPING =
        new Field(PropertyNd.class, "mapping", Node.class, 0, 1);

    private boolean m_isImmediate = false;
    private boolean m_isUnique = false;
    private boolean m_isComponent = false;
    private boolean m_isComposite = false;
    private Integer m_lower = null;
    private Integer m_upper = new Integer(1);

    private boolean m_isUnversioned = false;
    private boolean m_isVersioned = false;

    /**
     * @post isImmediate()
     **/
    public void setImmediate() {
        m_isImmediate = true;
    }

    public boolean isImmediate() {
        return m_isImmediate;
    }

    /**
     * @post isUnique()
     **/
    public void setUnique() {
        m_isUnique = true;
    }

    /**
     * @post isComponent()
     **/
    public void setComponent() {
        m_isComponent = true;
    }

    /**
     * @post isComposite()
     **/
    public void setComposite() {
        m_isComposite = true;
    }

    public Integer getLower() {
        return m_lower;
    }

    public void setLower(Integer lower) {
        m_lower = lower;
    }

    public Integer getUpper() {
        return m_upper;
    }

    public void setUpper(Integer upper) {
        m_upper = upper;
    }

    /**
     * @see #isVersioned()
     * @post isVersioned()
     **/
    public void setVersioned() {
        m_isVersioned = true;
    }

    /**
     * @see #isUnversioned()
     * @post isUnversioned()
     **/
    public void setUnversioned() {
        m_isUnversioned = true;
    }


    public boolean isUnique() {
        return m_isUnique;
    }

    public boolean isComponent() {
        return m_isComponent;
    }

    public boolean isComposite() {
        return m_isComposite;
    }

    public boolean isCollection() {
        return m_upper == null;
    }

    public boolean isNullable() {
        return m_lower == null || m_lower.intValue() == 0;
    }

    /**
     * Returns <code>true</code> if this property is marked "versioned" in the
     * PDL.
     *
     * @see #isUnversioned()
     * @see #setVersioned()
     **/
    public boolean isVersioned() {
        return m_isVersioned;
    }

    /**
     * Returns <code>true</code> if this property is marked "unversioned" in the PDL.
     * 
     * <p>Note that <code>isUnversioned()</code> is <em>not</em> the same as
     * <code>!isVersioned()</code>. If the property is marked neither
     * "versioned", nor "unversioned" (as most properties are), then both of
     * these methods return <code>false</code>. However, if one of them returns
     * <code>true</code>, then other returns <code>false</code>. </p>
     *
     * @see #isVersioned()
     * @see #setVersioned()
     **/
    public boolean isUnversioned() {
        return m_isUnversioned;
    }

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onProperty(this);
    }

    public IdentifierNd getName() {
        return (IdentifierNd) get(NAME);
    }

    public TypeNd getType() {
        return (TypeNd) get(TYPE);
    }

    public Node getMapping() {
        return (Node) get(MAPPING);
    }
}
