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
 * DataOperationNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/08/16 $
 **/

public class DataOperationNd extends Node {

    

    public static final Field NAME =
        new Field(DataOperationNd.class, "name", IdentifierNd.class, 1, 1);
    public static final Field SQL =
        new Field(DataOperationNd.class, "sql", SQLBlockNd.class, 1, 1);

    public void dispatch(Switch sw) {
        sw.onDataOperation(this);
    }

    public IdentifierNd getName() {
        return (IdentifierNd) get(NAME);
    }

    public SQLBlockNd getSQL() {
        return (SQLBlockNd) get(SQL);
    }

}
