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
package com.arsdigita.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.apache.log4j.Logger;

/**
 * A utility class for notifying the developer of the state of a Java
 * object.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Reporter.java 287 2005-02-22 00:29:02Z sskracic $
 */
public final class Reporter {

    private static final Logger s_log = Logger.getLogger(Reporter.class);

    private final Logger m_log;
    private final Object m_object;
    private BeanInfo m_info;

    public Reporter(final Logger log, final Object object) {
        this(log, object, object.getClass());
    }

    public Reporter(final Logger log,
                    final Object object,
                    final Class base) {
        Assert.exists(log, Logger.class);
        Assert.exists(object, Object.class);

        m_log = log;
        m_object = object;

        if (m_log.isDebugEnabled()) {
            final Class special = m_object.getClass();
            final Class general = base.getSuperclass();

            try {
                if (general == null) {
                    m_info = Introspector.getBeanInfo(special);
                } else {
                    m_info = Introspector.getBeanInfo(special, general);
                }
            } catch (IntrospectionException ie) {
                throw new UncheckedWrapperException(ie);
            }
        }
    }

    public final void mutated(final String property) {
        if (m_log.isDebugEnabled()) {
            Assert.exists(property, String.class);

            final PropertyDescriptor[] props = m_info.getPropertyDescriptors();

            for (int i = 0; i < props.length; i++) {
                final PropertyDescriptor prop = props[i];

                if (prop.getName().equals(property)) {
                    final Method method = prop.getReadMethod();

                    if (method != null) {
                        m_log.debug
                            (property + " set to " +
                             literal(value(method)) + " on " +
                             m_object);
                    }

                    break;
                }
            }
        }
    }

    public final void report() {
        if (m_log.isDebugEnabled()) {
            final PropertyDescriptor[] props = m_info.getPropertyDescriptors();

            m_log.debug("-*- " + m_object + " -*-");

            for (int i = 0; i < props.length; i++) {
                final Method method = props[i].getReadMethod();

                if (method != null) {
                    m_log.debug(print(method));
                }
            }
        }
    }

    private String print(final Method method) {
        final Class clacc = method.getReturnType();
        final Package pakkage = clacc.getPackage();
        final StringBuffer buffer = new StringBuffer(64);

        if (pakkage == null) {
            buffer.append(clacc.getName());
        } else {
            buffer.append
                (clacc.getName().substring(pakkage.getName().length() + 1));
        }

        buffer.append(" ");
        buffer.append(method.getName());

        final int len = 30 - buffer.length();

        if (len > 0) {
            final char[] spacer = new char[len];

            Arrays.fill(spacer, ' ');

            buffer.insert(0, spacer);
        }

        buffer.append("() -> ");
        buffer.append(literal(value(method)));

        return buffer.toString();
    }

    private String literal(final Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof String) {
            return "\"" + object + "\"";
        } else {
            return object.toString();
        }
    }

    private Object value(final Method method) {
        try {
            return method.invoke(m_object, new Object[] {});
        } catch (IllegalAccessException iae) {
            throw new UncheckedWrapperException(iae);
        } catch (InvocationTargetException ite) {
            throw new UncheckedWrapperException(ite);
        }
    }
}
