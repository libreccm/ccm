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
package com.arsdigita.toolbox.ui;



import java.util.Iterator;
import com.arsdigita.bebop.list.ListModel;




/**
 * <p>Simple adapter to build a Bebop ListModel from a Java Iterator.</p>
 **/
public class IteratorListModel implements ListModel {

    /**
     * <p>An instance of this interface may be passed to
     * IteratorListModel's constructor for those situations where
     * element.toString() does not produce an appropriate key for a
     * ListModel.</p>
     **/
    public static interface KeyFunction {
        public String getKey(Object object);
    }

    Iterator m_iterator;
    KeyFunction m_keyFunction;
    Object m_current;

    public IteratorListModel(Iterator iterator,
                             KeyFunction keyFunction) {
        m_iterator = iterator;
        m_keyFunction = keyFunction;
    }

    public IteratorListModel(Iterator iterator) {
        this(iterator, null);
    }

    public Object getElement() {
        return m_current;
    }

    public String getKey() {
        if (m_keyFunction != null) {
            return m_keyFunction.getKey(m_current);
        } else {
            return m_current.toString();
        }
    }

    public boolean next() {
        if (!m_iterator.hasNext()) {
            return false;
        }
        m_current = m_iterator.next();
        return true;
    }
}
