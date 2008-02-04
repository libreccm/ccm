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

/**
 * This is the base class for exceptions in persistence. By default these
 * exceptions are assumed to be internal exceptions - bugs in the
 * implementation. If code in the implementation determines the exception is
 * fault of the client, the exception is flagged so that it is not internal.
 * At the top level internal exceptions are converted to
 * UncheckedWrapperExceptions so that they will not be caught as persistence
 * exceptions.
 **/
public abstract class ProtoException extends RuntimeException {

    public static class Role {

	private String m_name;

	Role(String name) {
	    m_name = name;
	}

	public String toString() {
	    return m_name;
	}

    }

    public static final Role OBJECT = new Role("object");
    public static final Role PROPERTY = new Role("property");
    public static final Role VALUE = new Role("value");

    // internal or external exception
    private boolean m_internal = true;

    protected ProtoException() { super(); }

    protected ProtoException(boolean internal) {
        super();
        setInternal(internal);
    }

    protected ProtoException(String msg) { super(msg); }

    protected ProtoException(String msg, boolean internal) {
        super(msg);
        setInternal(internal);
    }

    void setInternal(boolean internal) { m_internal = internal; }

    boolean isInternal() { return m_internal; }
}
