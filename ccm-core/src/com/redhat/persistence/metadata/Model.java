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
 * Model.
 * 
 * A dot-separated String of names.
 * The part after the last dot is the 'name' of the model.
 * The part before the last dot is the 'parent', or the path to the name.
 * 
 * usually a package name.
 * , used to connect a class to pdl (Data Object
 * Type, usually BASE_DATA_OBJECT_TYPE)
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 */
public class Model {

    /** Map containing the model dot separated parts. The key contains each 
     *  part of the dot-separated string, the value part the
     *  (still dot-separated)path to that key (it's parent).                 */
    private static final HashMap MODELS = new HashMap();

    /** 
     * Get a Modal instance. Singelton pattern!
     */
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
                        //recursively deconstructs the model string to it's parts.
                        parent = getInstance(model.substring(0, dot));
                        name = model.substring(dot + 1);
                    } else {
                        // finally deconstructed, name is now the (originally)
                        // first part of the model without a parent.
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

    /** The parent model of the injected model (reconstructed doku)           */
    private final Model m_parent;
    /** The name (last part) of the injected model (reconstructed doku)       */
    private final String m_name;
    /** The ????  of the injected model (reconstructed doku)           */
    private final String m_qualifiedName;

    /**
     * Private Constructor to instantiate a Model. 
     * @param parent
     * @param name 
     */
    private Model(Model parent, String name) {
        m_parent = parent;
        m_name = name;
        if (m_parent == null) {
            m_qualifiedName = m_name;
        } else {
            m_qualifiedName = m_parent.getQualifiedName() + "." + m_name;
        }

    }

    /**
     * Getter ...
     * @return 
     */
    public Model getParent() {
        return m_parent;
    }

    /**
     * Getter ..
     * @return 
     */
    public String getName() {
        return m_name;
    }

    /**
     * Getter... 
     * @return 
     */
    public String getQualifiedName() {
        return  m_qualifiedName;
    }

}
