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
package com.redhat.persistence;

import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.Adapter;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * RecordSet
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Id: RecordSet.java 287 2005-02-22 00:29:02Z sskracic $
 **/

public abstract class RecordSet {

    private static final Logger LOG = Logger.getLogger(RecordSet.class);

    private Signature m_signature;

    protected RecordSet(Signature signature) {
        m_signature = signature;
    }

    protected Signature getSignature() {
        return m_signature;
    }

    boolean isFetched(Path path) {
        return m_signature.isFetched(path);
    }

    public abstract boolean next();

    public abstract Object get(Path p);

    public abstract void close();

    Map load(Session ssn) {
        Collection paths = m_signature.getPaths();

	LinkedList remaining = new LinkedList();
	for (Iterator it = paths.iterator(); it.hasNext(); ) {
	    Path path = (Path) it.next();

            for (; ; path = path.getParent()) {
                if (!remaining.contains(path)) {
                    remaining.add(path);
                }
                if (m_signature.isSource(path)) {
                    break;
                }
            }
        }

	HashMap values = new HashMap();
	int before;
	do {
	    before = remaining.size();

	    OUTER: for (Iterator it = remaining.iterator(); it.hasNext(); ) {
		Path p = (Path) it.next();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("loading " + p);
                }
		ObjectType type = m_signature.getType(p);
                Adapter adapter = ssn.getRoot().getAdapter(type);
		Collection props = type.getImmediateProperties();
		if (props.size() == 0) {
		    values.put(p, get(p));
		    it.remove();
		} else {
		    PropertyMap pmap = new PropertyMap(type);
		    for (Iterator iter = props.iterator(); iter.hasNext(); ) {
			Property prop = (Property) iter.next();
			Path kp = Path.add(p, prop.getName());
			if (values.containsKey(kp)) {
			    pmap.put(prop, values.get(kp));
			} else if (m_signature.isFetched(kp)) {
			    continue OUTER;
			}
		    }

		    Object obj = null;
		    if (!pmap.isNull()) {
                        Object previous = null;
			if (type.isKeyed()) {
                            previous = ssn.getObject
                                (adapter.getSessionKey(type, pmap));

                            if (previous != null) {
                                ObjectType prevType =
                                    adapter.getObjectType(previous);

                                if (type.equals(prevType)
                                    || prevType.isSubtypeOf(type)) {
                                    obj = previous;
                                } else if (!type.isSubtypeOf(prevType)) {
                                    throw new IllegalStateException
                                        ("object of wrong type in session "
                                         + type + " " + prevType);
                                }
                            }
			}

			if (obj == null) {
			    obj = adapter.getObject(type, pmap);
                            if (previous != obj) {
                                ssn.use(obj);
                            }
			}
		    }
		    values.put(p, obj);
		    it.remove();
		}
	    }
	} while (remaining.size() < before);

	if (remaining.size() > 0) {
	    throw new IllegalStateException
		("unable to load the following paths: " + remaining +
		 "\nsignature: " + m_signature);
	}

        HashMap cursorValues = new HashMap();

	for (Iterator it = values.entrySet().iterator(); it.hasNext(); ) {
	    Map.Entry me = (Map.Entry) it.next();
	    Path p = (Path) me.getKey();
            Object value = me.getValue();

	    if (m_signature.isSource(p)) {
                cursorValues.put(p, value);
                continue;
            }

	    Property prop = m_signature.getProperty(p);
            if (prop.getContainer().isKeyed() && !prop.isCollection()) {
		Object container = values.get(p.getParent());
		if (container == null) {
		    if (value == null) {
			continue;
		    } else {
			throw new IllegalStateException
                            ("container of " + p + " is null");
		    }
		}
		ssn.load(container, prop, value);
	    } else {
                cursorValues.put(p, value);
            }
	}

	return cursorValues;
    }

}
