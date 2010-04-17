/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.bebop.jsp;

import com.arsdigita.util.Assert;

/**
 * A wrapper exception that can be used to rethrow another
 * exception.
 *
 * The standard JspException doesn't accept a root cause,
 * hence this subclass.
 *
 * @author David Eison 
 * @version $Id: JspWrapperException.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class JspWrapperException extends RuntimeException {

    Throwable m_rootCause;

    /**
     * Constructor which takes a root cause
     * that this exception will be wrapping.
     */
    public JspWrapperException (Throwable rootCause) {
        this(null, rootCause);
    }

    /**
     * Constructor which takes a message string and a root cause
     * that this exception will be wrapping.  The message string
     * should be something different than rootCause.getMessage()
     * would normally provide.
     */
    public JspWrapperException (String s, Throwable rootCause) {
        super(s);
        Assert.exists(rootCause);
        this.m_rootCause = rootCause;
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
        b.append(" (root cause: ").append(m_rootCause.toString());
        b.append(")");
        return b.toString();
    }

    /**
     * This exception's message and the root cause's.
     */
    public String getMessage() {
        if (m_rootCause != null) {
            return super.getMessage() + " (root cause: " + m_rootCause.getMessage() + ")";
        } else {
            return super.getMessage();
        }
    }

    /**
     * Stack trace for the root cause.
     */
    public void printStackTrace() {
        super.printStackTrace();
        System.err.print("Root cause: ");
        m_rootCause.printStackTrace();
    }

    /**
     * Stack trace for the root cause.
     */
    public void printStackTrace(java.io.PrintStream s) {
        super.printStackTrace(s);
        s.println("Root cause: ");
        m_rootCause.printStackTrace(s);
    }

    /**
     * Stack trace for the root cause.
     */
    public void printStackTrace(java.io.PrintWriter s) {
        super.printStackTrace(s);
        s.println("Root cause: ");
        m_rootCause.printStackTrace(s);
    }
}
