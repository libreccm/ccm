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
package com.arsdigita.persistence;

import com.arsdigita.util.UncheckedWrapperException;

/**
 * PersistenceException
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #14 $ $Date: 2004/08/16 $
 */

public class PersistenceException extends UncheckedWrapperException {

    public final static String versionId = "$Id: PersistenceException.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     * Constructor for a PersistenceException which does not wrap
     * another exception.  If wrapping another exception,
     * PersistenceException.newInstance(...) should be used.
     *
     * @see #newInstance(Throwable)
     */
    public PersistenceException(String message) {
        super(message, null);
    }

    /**
     * Constructor which takes a root cause
     * that this exception will be wrapping.
     */
    protected PersistenceException(Throwable rootCause) {
        this(null, rootCause);
    }

    /**
     * Constructor which takes a message string and a root cause
     * that this exception will be wrapping.  The message string
     * should be something different than rootCause.getMessage()
     * would normally provide.
     */
    protected PersistenceException(String s, Throwable rootCause) {
        super(s, rootCause);
        // TODO: Consider adding verification that if rootCause is
        // a uniqueconstraintexception or dbnotavailableexception,
        // then so is this persistenceexception.  To guard against
        // people calling this constructor inappropriately.
        // Not doing right now because it a) seems like overkill,
        // b) could result in a legit error message being eaten and
        // replaced with something else entirely at runtime when
        // weird exceptions happened; i.e. it would be hard to
        // fully test.
    }


    /**
     * It's not necessary to use newInstance for just a string argument, but
     * you can if you'd prefer to remain consistent in PersistenceException
     * creation.
     */
    public static final PersistenceException newInstance(String s) {
        return new PersistenceException(s);
    }

    /**
     * This method should be used to create a persistence exception wrapping
     * another exception, to allow for creation of a particular subtype of
     * persistence exception based on the type of the rootCause passed in.
     */
    public static final PersistenceException newInstance(Throwable rootCause) {
        return newInstance(null, rootCause);
    }

    /**
     * This method should be used to create a persistence exception wrapping
     * another exception, to allow for creation of a particular subtype of
     * persistence exception based on the type of the rootCause passed in.
     */
    public static final PersistenceException newInstance(String s,
                                                         Throwable rootCause) {
        if (rootCause instanceof com.redhat.persistence.FlushException) {
            return new FlushException
                (s, (com.redhat.persistence.FlushException) rootCause);
        }
        return new PersistenceException(s, rootCause);
    }
}
