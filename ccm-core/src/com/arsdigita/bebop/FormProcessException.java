/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.bebop;

import javax.servlet.ServletException;

/**
 * This class represents exceptions that occur within the processing
 * methods of any of the form event listeners.  Typically the code
 * will catch specific exceptions such as <code>SQLException</code>
 * and rethrow them as instances of this class to pass the message to
 * the controller in a standard fashion.
 *
 * <p>Since this class is a subclass of <code>ServletException</code>,
 * servlets that do form processing within a <code>doPost</code> or
 * <code>doGet</code> methods do not need to explicitly catch
 * instances of this class.  However, they may wish to do so for
 * special error reporting to the user, or to notify the webmaster via
 * e-mail of the problem. */

public class FormProcessException extends ServletException {

    public static final String versionId = "$Id: FormProcessException.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public FormProcessException(String message) {
        super(message);
    }

    public FormProcessException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public FormProcessException(Throwable rootCause) {
        super(rootCause);
    }

    /**
     * In addition to printing the stack trace for this exception, also prints
     * the stack trace for the root cause, if any.  This is a workaround for
     * those implementations of {@link ServletException} that don't implement
     * <code>printStackTrace</code> correctly.  If you happen to use an
     * implementation that does, the stack trace for the root cause may be
     * printed twice, which is not that big of a deal in the grand scheme of
     * things.
     */
    public void printStackTrace() {
        super.printStackTrace();
        if (getRootCause() != null) {
            System.err.print("Root cause: ");
            getRootCause().printStackTrace();
        }
    }

    /**
     * @see #printStackTrace()
     */
    public void printStackTrace(java.io.PrintStream s) {
        super.printStackTrace(s);
        if (getRootCause() != null) {
            s.println("Root cause: ");
            getRootCause().printStackTrace(s);
        }
    }

    /**
     * @see #printStackTrace()       
     */
    public void printStackTrace(java.io.PrintWriter s) {
        super.printStackTrace(s);
        if (getRootCause() != null) {
            s.println("Root cause: ");
            getRootCause().printStackTrace(s);
        }
    }

    /**
     * <p>Returns the concatenation of {@link #getMessage()} and {@link
     * #getRootCause()}.<code>getMessage()</code>.</p>
     **/
    public String getMessages() {
        StringBuffer result = new StringBuffer(getMessage());
        if ( getRootCause() != null ) {
            result.append(" (root cause: ")
                .append(getRootCause().getMessage())
                .append(")");
        }
        return result.toString();
    }
}
