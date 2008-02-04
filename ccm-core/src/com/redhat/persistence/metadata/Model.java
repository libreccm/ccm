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
package com.redhat.persistence.metadata;

import java.util.HashMap;

/**
 * Model
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 **/

public class Model {

    public final static String versionId = "$Id: Model.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final HashMap MODELS = new HashMap();

    public static final Model getInstance(String model) {
        if (model == null) {
            return null;
        }

        Model result;
        
        if (MODELS.containsKey(model)) {
            result = (Model) MODELS.get(model);
        } else {
            synchronized (MODELS) {
                if (MODELS.containsKey(model)) {
                    result = (Model) MODELS.get(model);
                } else {
                    int dot = model.lastIndexOf('.');
                    Model parent;
                    String name;
                    if (dot > -1) {
                        parent = getInstance(model.substring(0, dot));
                        name = model.substring(dot + 1);
                    } else {
                        parent = null;
                        name = model;
                    }

                    result = new Model(parent, name);
                    MODELS.put(model, result);
                }
            }
        }

        return result;
    }

    private final Model m_parent;
    private final String m_name;
    private final String m_qualifiedName;

    private Model(Model parent, String name) {
        m_parent = parent;
        m_name = name;
        if (m_parent == null) {
            m_qualifiedName = m_name;
        } else {
            m_qualifiedName = m_parent.getQualifiedName() + "." + m_name;
        }

    }

    public Model getParent() {
        return m_parent;
    }

    public String getName() {
        return m_name;
    }

    public String getQualifiedName() {
        return  m_qualifiedName;
    }

}
