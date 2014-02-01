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

import java.util.Collection;

/**
 * File
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/

public class FileNd extends Node {

    

    public static final Field MODEL =
        new Field(FileNd.class, "model", ModelNd.class, 1, 1);
    public static final Field IMPORTS =
        new Field(FileNd.class, "imports", ImportNd.class);
    public static final Field OBJECT_TYPES =
        new Field(FileNd.class, "objectTypes", ObjectTypeNd.class);
    public static final Field ASSOCIATIONS =
        new Field(FileNd.class, "associations", AssociationNd.class);
    public static final Field DATA_OPERATIONS =
        new Field(FileNd.class, "dataOperations", DataOperationNd.class);

    private String m_name;

    public FileNd(String name) {
        m_name = name;
    }

    public String getName() {
        return m_name;
    }

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onFile(this);
    }

    public FileNd getFile() {
        return this;
    }

    public ModelNd getModel() {
        return (ModelNd) get(MODEL);
    }

    public Collection getImports() {
        return (Collection) get(IMPORTS);
    }

}
