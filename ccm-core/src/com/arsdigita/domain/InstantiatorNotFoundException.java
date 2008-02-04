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
package com.arsdigita.domain;

import com.arsdigita.persistence.PersistenceException;

/**
 * This exception is thrown by {@link DomainObjectFactory} when
 * it is unable to find an instantiator for a given <code>DataObject</code>.
 *
 * @author Stanislav Freidin 
 * @version $Revision: #8 $
 */

public class InstantiatorNotFoundException extends PersistenceException {

    public final static String versionId = "$Id: InstantiatorNotFoundException.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     * Constructor for an InstantiatorNotFoundException which does not wrap
     * another exception.
     */
    public InstantiatorNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor which takes a root cause
     * that this exception will be wrapping.
     */
    protected InstantiatorNotFoundException(Throwable rootCause) {
        super(rootCause);
    }

    /**
     * Constructor which takes a message string and a root cause
     * that this exception will be wrapping.  The message string
     * should be something different than rootCause.getMessage()
     * would normally provide.
     */
    protected InstantiatorNotFoundException(String s, Throwable rootCause) {
        super(s, rootCause);
    }

}
