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
import com.redhat.persistence.metadata.ObjectMap;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.oql.Expression;
import com.redhat.persistence.oql.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * Signature
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Id: Signature.java 738 2005-09-01 12:36:52Z sskracic $
 **/

public class Signature {

    private static final Logger s_log = Logger.getLogger(Signature.class);

    private ArrayList m_paths = new ArrayList();
    private ArrayList m_sources = new ArrayList();
    private HashMap m_sourceMap = new HashMap();

    public Signature() { }

    public Signature(Signature sig) {
        m_paths.addAll(sig.m_paths);
        m_sources.addAll(sig.m_sources);
        m_sourceMap.putAll(sig.m_sourceMap);
    }

    public Signature(ObjectType type) {
        addSource(type, null);
    }

    public Query makeQuery(Expression expr) {
        addDefaultProperties();

        if (m_paths.size() == 0) {
            throw new IllegalStateException(this + "\n");
        }

        Query q = new Query(expr);

        for (Iterator it = m_paths.iterator(); it.hasNext(); ) {
            Path path = (Path) it.next();
            if (path == null) { continue; }
            q.fetch(getColumn(path), Expression.valueOf(path));
        }

        return q;
    }

    public String getColumn(Path p) {
        final int size = 30;
        final String path = p.getPath();
        if (path.length() <= size) {
            return path;
        }

        final int pathIndex = m_paths.indexOf(p);

        // compute String.valueOf(pathIndex).length() without creating a
        // throw-away String object.
        int pathIdxWidth;
        if (pathIndex == 0) {
            pathIdxWidth = 1;
        } else {
            pathIdxWidth = 0;
            for (int i = pathIndex; i > 0; i /= 10) { pathIdxWidth++; }
        }

        StringBuffer result = new StringBuffer(size);
        result.append(path.substring(0, size - pathIdxWidth));
        result.append(pathIndex);

        return result.toString();
    }

    public ObjectType getObjectType() {
        return getSource(null).getObjectType();
    }

    public boolean hasPath(Path p) {
        return m_paths.contains(p);
    }

    public void addPath(String path) {
        addPath(Path.get(path));
    }

    /**
     * Add all leaves of key property hierarchy
     */
    private void addPathImmediates(Path path) {
	ObjectType type = getType(path);
        Collection props = type.getImmediateProperties();
        // all props for unkeyed, immediate only for keyed

        if (props.size() == 0 && !isSource(path)) {
            if (!m_paths.contains(path)) {
                m_paths.add(path);
            }
        } else {
            for (Iterator it = props.iterator(); it.hasNext(); ) {
                Property prop = (Property) it.next();
		addPathImmediates(Path.add(path, prop.getName()));
            }
        }
    }

    private void makePathLoadable(Path prefix, Collection paths) {
        for (Iterator it = paths.iterator(); it.hasNext(); ) {
            Path p = (Path) it.next();
            Path path;
            if (prefix == null) {
                path = p;
            } else {
                path = Path.add(prefix, p);
            }
            makePathLoadable(path);
        }
    }

    private void makePathLoadable(Path path) {
        addPathImmediates(path);
        // XXX: forcing container id properties to be loaded
        // this does not need to be done here. could push to wrapper layer
        // and change RecordSet to deal with null containers by passing
        // value to Cursor and loading in session
        if (!isSource(path)) {
            Path parent = path.getParent();
            if (!m_paths.contains(parent)) {
                makePathLoadable(parent);
            }
        }
    }

    public void addPath(Path path) {
	if (!exists(path)) {
	    throw new NoSuchPathException(path);
	}

        if (path == null) { return; }

        makePathLoadable(path);
        addDefaultProperties(path);
    }

    public Collection getPaths() {
        return m_paths;
    }

    public boolean isFetched(Path path) {
        // XXX: isFetched(null) ?
        if (path == null) { return true; }

 	for (Iterator it = getPaths().iterator(); it.hasNext(); ) {
	    Path p = (Path) it.next();
	    if (path.isAncestor(p)) {
		return true;
	    }
	}

	return false;
    }

    public void addSignature(Signature sig, Path path) {
        for (Iterator it = sig.m_sources.iterator(); it.hasNext(); ) {
            Source source = (Source) it.next();
            addSource
                (source.getObjectType(), Path.add(path, source.getPath()));
        }
        for (Iterator it = sig.m_paths.iterator(); it.hasNext(); ) {
            Path p = Path.add(path, (Path) it.next());
            if (!m_paths.contains(p)) {
                m_paths.add(p);
            }
        }
    }

    public void addSource(ObjectType type, Path path) {
        addSource(new Source(type, path));
    }

    public void addSource(Source s) {
        if (s == null) {
            throw new IllegalArgumentException
                ("Cannot add a null source.");
        }

        if (m_sourceMap.containsKey(s.getPath())) {
            throw new IllegalArgumentException
                ("Query already contains a source for that path: " +
                 s.getPath());
        }

        m_sources.add(s);
        m_sourceMap.put(s.getPath(), s);
    }

    public Source getSource(Path p) {
        if (isSource(p)) {
            return (Source) m_sourceMap.get(p);
        } else {
            return null;
        }
    }

    public boolean isSource(Path p) {
        return m_sourceMap.containsKey(p);
    }

    public Collection getSources() {
        return m_sources;
    }

    private void addPathImmediates(Path prefix, Collection paths) {
        for (Iterator it = paths.iterator(); it.hasNext(); ) {
            Path p = (Path) it.next();
            Path path;
            if (prefix == null) {
                path = p;
            } else {
                path = Path.add(prefix, p);
            }
            addPathImmediates(path);
        }
    }

    private void addDefaultProperties(Path path) {
        ObjectType type = getType(path);
        addFetchedPaths(path, type);

        if (!isSource(path)) {
            Root root = type.getRoot();
            Property prop = getProperty(path);
            // assume that path.getParent() is keyed
            ObjectMap container = root.getObjectMap(prop.getContainer());
            if (container != null) {
                makePathLoadable(path.getParent(), container.getDeclaredFetchedPaths());
            }
        }
    }

    private void addFetchedPaths(Path path, ObjectType type) {
        Root root = type.getRoot();
        if (root == null) { return; }
        ObjectMap om = root.getObjectMap(type);
        makePathLoadable(path, om.getFetchedPaths());
    }

    private void addDefaultProperties() {
        for (Iterator it = m_sources.iterator(); it.hasNext(); ) {
            Source source = (Source) it.next();
            makePathLoadable(source.getPath());
            addFetchedPaths(source.getPath(), source.getObjectType());
        }
    }

    public Property getProperty(Path path) {
        Path parent = path.getParent();
        if (isSource(parent)) {
            return getSource(parent).getObjectType().getProperty
                (path.getName());
        } else {
            Property prop = getProperty(parent);
            if (prop == null) {
                throw new IllegalArgumentException
                    ("no such property in signature: " + path);
            } else {
                return prop.getType().getProperty(path.getName());
            }
        }
    }

    public ObjectType getType(Path path) {
        if (isSource(path)) {
	    return getSource(path).getObjectType();
	} else {
	    return getProperty(path).getType();
	}
    }

    public boolean exists(Path p) {
        if (isSource(p)) {
            return true;
        }

        if (p == null) {
            return false;
        }

        return exists(p.getParent()) &&
            getType(p.getParent()).getProperty(p.getName()) != null;
    }

    public String toString() {
        return "Paths are  " + m_paths + ", sources are " + m_sources;
    }
}
