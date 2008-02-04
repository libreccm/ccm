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
package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.common.Path;

class SpliceEnvironment extends Environment {

    private Environment m_base;
    private Path m_path;
    private Environment m_splice;

    public SpliceEnvironment(Environment base, Path path, Environment splice) {
        super(base.getEngine(), null);
        m_base = base;
        m_path = path;
        m_splice = splice;
    }

    private Path unsplice(Path path) {
	return Path.get(":" + m_path.getRelative(path).getPath());
    }

    public boolean contains(Path path) {
        if (m_path.isAncestor(path)) {
            return m_splice.contains(unsplice(path));
        } else {
            return m_base.contains(path);
        }
    }

    public void set(Path parameter, Object value) {
        if (m_path.isAncestor(parameter)) {
	    m_splice.set(unsplice(parameter), value);
	} else {
	    m_base.set(parameter, value);
	}
    }

    public void set(Path parameter, Object value, int type) {
        if (m_path.isAncestor(parameter)) {
	    m_splice.set(unsplice(parameter), value, type);
	} else {
	    m_base.set(parameter, value, type);
	}
    }

    public Object get(Path parameter) {
        if (m_path.isAncestor(parameter)) {
            return m_splice.get(unsplice(parameter));
        } else {
            return m_base.get(parameter);
        }
    }

    public int getType(Path parameter) {
        if (m_path.isAncestor(parameter)) {
            return m_splice.getType(unsplice(parameter));
        } else {
            return m_base.getType(parameter);
        }
    }

    public String toString() {
        return "<env " + m_base + " splice on " + m_path + " with " +
            m_splice + ">";
    }

}
