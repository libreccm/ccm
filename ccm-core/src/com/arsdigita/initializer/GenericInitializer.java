/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.initializer;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 */
public final class GenericInitializer implements Initializer {
    private final Configuration m_config = new Configuration();
    private final String m_name;

    public GenericInitializer(final String name) {
        if (name == null) throw new IllegalArgumentException();

        m_name = name;
    }

    public Configuration getConfiguration() {
        return m_config;
    }

    public void startup() throws InitializationException { /* empty */ }
    public void shutdown() throws InitializationException { /* empty */ }

    public String toString() {
        return m_name;
    }

    public boolean equals(final Object other) {
        if (other != null && other instanceof GenericInitializer) {
            return m_name.equals(((GenericInitializer) other).m_name);
        } else {
            return super.equals(other);
        }
    }

    public int hashCode() {
        return m_name.hashCode();
    }
}
