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
package com.arsdigita.kernel.security;

import javax.security.auth.login.LoginException;
import com.arsdigita.util.Exceptions;
import com.arsdigita.util.ExceptionUnwrapper;


public class KernelLoginException extends LoginException {

    static {
        Exceptions.registerUnwrapper(
            KernelLoginException.class,
            new ExceptionUnwrapper() {
                public Throwable unwrap(Throwable t) {
                    KernelLoginException ex = (KernelLoginException)t;
                    return ex.getRootCause();
                }
            });
    }
    
    private Throwable m_rootCause;

    public KernelLoginException() {
        this((Throwable) null);
    }

    public KernelLoginException(String message) {
        this(message, null);
    }

    public KernelLoginException(Throwable rootCause) {
        super();
        
        m_rootCause = rootCause;
    }

    public KernelLoginException(String msg, Throwable rootCause) {
        super(msg);

        m_rootCause = rootCause;
    }

    /**
     * Indicates if this exception has a root cause.
     */
    public boolean hasRootCause() {
        return m_rootCause != null;
    }

    /**
     * Gets the root cause of this exception.
     */
    public Throwable getRootCause() {
        return m_rootCause;
    }

    // All further methods override normal throwable behavior to
    // combine information w/ the root cause.

    /**
     * Get a string representing this exception and the root cause.
     */
    public String toString() {
        return toString(this.getClass());
    }

    /**
     * Get a string representing this exception and the root cause.
     *
     * Functions like normal toString, except that the name of the
     * provided class will be used instead of the name of the
     * unchecked wrapper exception.  Useful when another exception
     * class is using  an unchecked wrapper exception to delegate
     * to.
     */
    public String toString(Class delegatingClass) {
        // default toString calls getMessage, so we don't want to rely on it
        // here.
        StringBuffer b = new StringBuffer(delegatingClass.getName());
        String superMsg = super.getMessage();
        if (superMsg != null) {
            b.append(": ").append(superMsg);
        }
        if (m_rootCause != null) {
            b.append(" (root cause: ").append(m_rootCause.toString());
            b.append(")");
        }
        return b.toString();
    }

    /**
     * This exception's message and the root cause's.
     */
    public String getMessage() {
        if (m_rootCause != null) {
            return super.getMessage() + " (root cause: " + 
                m_rootCause.getMessage() + ")";
        } else {
            return super.getMessage();
        }
    }

    /**
     * Stack trace for the root cause.
     */
    public void printStackTrace() {
        super.printStackTrace();
        if (m_rootCause != null) {
            System.err.print("Root cause: ");
            m_rootCause.printStackTrace();
        }
    }

    /**
     * Stack trace for the root cause.
     */
    public void printStackTrace(java.io.PrintStream s) {
        super.printStackTrace(s);
        if (m_rootCause != null) {
            s.println("Root cause: ");
            m_rootCause.printStackTrace(s);
        }
    }

    /**
     * Stack trace for the root cause.
     */
    public void printStackTrace(java.io.PrintWriter s) {
        super.printStackTrace(s);
        if (m_rootCause != null) {
            s.println("Root cause: ");
            m_rootCause.printStackTrace(s);
        }
    }
}
