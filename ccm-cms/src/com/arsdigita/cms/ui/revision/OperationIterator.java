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
package com.arsdigita.cms.ui.revision;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.versioning.Difference;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: OperationIterator.java 1942 2009-05-29 07:53:23Z terry $
 */
class OperationIterator {

    private static final Logger s_log = Logger.getLogger
        (OperationIterator.class);

    private final Iterator m_operations;

    OperationIterator(final Difference diff) {
        final Iterator changes = diff.getChanges().iterator();

        final ArrayList operations = new ArrayList();

        while (changes.hasNext()) {
            final Difference.Change change =
                (Difference.Change) changes.next();

            final ObjectType type = change.getOID().getObjectType();

            String name;

            if (type.hasProperty("displayName")) {
                name = String.valueOf
                    (change.getTo(type.getProperty("displayName")));
            } else {
                name = type.toString();
            }

            final Iterator props = type.getProperties();

            while (props.hasNext()) {
                final Property prop = (Property) props.next();
                final Object from = change.getFrom(prop);
                final Object to = change.getTo(prop);

                if (blacklisted(prop)) continue;
                if (!change.isModified(prop)) continue;
                if (from == null && to == null) continue;
                if (from != null && from.equals(to)) continue;
                if (to != null && to.equals(from)) continue;

                final Iterator added = change.getAdded(prop).iterator();

                StringBuffer buffer = new StringBuffer();

                while (added.hasNext()) {
                    buffer.append(prettify(added.next(), change));
                    buffer.append(", ");
                }

                String adds = buffer.toString();

                if (adds.endsWith(", ")) {
                    adds = adds.substring(0, adds.length() - 2);
                }

                final Iterator removed = change.getRemoved(prop).iterator();

                buffer = new StringBuffer();

                while (removed.hasNext()) {
                    buffer.append(prettify(removed.next(), change));
                    buffer.append(", ");
                }

                String removes = buffer.toString();

                if (removes.endsWith(", ")) {
                    removes = removes.substring(0, removes.length() - 2);
                }

                final Object[] operation = new Object[] {
                    prettify(change.getOID(), change),
                    capitalize(prop.getName()),
                    new Boolean(prop.isCollection()),
                    prettify(from, change),
                    prettify(to, change),
                    adds,
                    removes
                };

                operations.add(operation);
            }
        }

        m_operations = operations.iterator();
    }

    private String prettify(final Object obj,
                            final Difference.Change change) {
        if (obj == null) {
            return lz("cms.ui.item.revision.nothing");
        } else if (obj instanceof String) {
            return quote(obj);
        } else if (obj instanceof OID) {
            return name((OID) obj, change);
        } else {
            return obj.toString();
        }
    }

    private String quote(final Object obj) {
        return "\"" + obj + "\"";
    }

    private String name(final OID oid, final Difference.Change change) {
        final ObjectType type = oid.getObjectType();

        if (type.hasProperty(ACSObject.DISPLAY_NAME)) {
            return String.valueOf
                (change.getTo(type.getProperty(ACSObject.DISPLAY_NAME)));
        } else {
            return type.toString();
        }
    }

    private boolean blacklisted(final Property prop) {
        final String name = prop.getName();

        return (name.equals(ContentItem.CHILDREN)
                || name.equals(ContentItem.CONTENT_SECTION));
    }

    private String capitalize(final String str) {
        if (str == null) {
            return "";
        } else if (str.length() == 0) {
            return "";
        } else {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
    }

    final boolean hasNext() {
        return m_operations.hasNext();
    }

    final Object[] next() {
        return (Object[]) m_operations.next();
    }

    private static GlobalizedMessage gz(final String key) {
        return GlobalizationUtil.globalize(key);
    }

    private static String lz(final String key) {
        return (String) gz(key).localize();
    }
}
