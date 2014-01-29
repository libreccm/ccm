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

import com.redhat.persistence.common.Path;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Mapping
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/08/16 $
 **/

public abstract class Mapping extends Element {

    

    public static abstract class Switch {
        public abstract void onValue(Value m);
        public abstract void onJoinTo(JoinTo m);
        public abstract void onJoinFrom(JoinFrom m);
        public abstract void onJoinThrough(JoinThrough m);
        public abstract void onStatic(Static m);
        public void onQualias(Qualias q) {
            throw new UnsupportedOperationException();
        }
    }

    private Path m_path;
    private SQLBlock m_retrieve;
    private ArrayList m_adds = null;
    private ArrayList m_removes = null;

    protected Mapping(Path path) {
        m_path = path;
    }

    public ObjectMap getObjectMap() {
        return (ObjectMap) getParent();
    }

    public Path getPath() {
        return m_path;
    }

    public abstract Table getTable();

    public SQLBlock getRetrieve() {
        return m_retrieve;
    }

    public void setRetrieve(SQLBlock retrieve) {
        m_retrieve = retrieve;
    }

    public Collection getAdds() {
        return m_adds;
    }

    public void setAdds(Collection adds) {
        if (adds == null) {
            m_adds = null;
        } else {
            m_adds = new ArrayList();
            m_adds.addAll(adds);
        }
    }

    public Collection getRemoves() {
        return m_removes;
    }

    public void setRemoves(Collection removes) {
        if (removes == null) {
            m_removes = null;
        } else {
            m_removes = new ArrayList();
            m_removes.addAll(removes);
        }
    }

    public abstract void dispatch(Switch sw);

    Object getElementKey() {
        return getPath();
    }

    public String toString() {
        return "<mapping for " + m_path + ">";
    }

}
