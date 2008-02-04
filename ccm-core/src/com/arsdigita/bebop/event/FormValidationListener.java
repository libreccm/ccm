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
package com.arsdigita.bebop.event;

import com.arsdigita.bebop.FormProcessException;
import java.util.EventListener;

/**
 *    Defines the interface for a class that implements a validation check
 *    on a set of form data.
 *
 *    @author Karl Goldstein 
 *    @author Uday Mathur 
 *    @version $Id: FormValidationListener.java 287 2005-02-22 00:29:02Z sskracic $
 */

public interface FormValidationListener extends EventListener {

    public static final String versionId = "$Id: FormValidationListener.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     * Performs a validation check on the specified <tt>FormData</tt>
     * object, involving any number of parameters.
     *
     * <p>The check is always performed after all HTTP request
     * parameters have been converted to data objects and stored in the
     * FormData object.
     *
     * <p>If a validation error is encountered, the <tt>setError</tt>
     * method of the <tt>FormData</tt> object may be used to set an
     * error message for reporting back to the user.
     *
     * <p>This method is responsible for catching any exceptions that
     * may occur during the validation.  These exceptions may either
     * be handled internally, or if they are unrecoverable may be
     * rethrown as instances of <code>FormProcessException</code>.
     *
     * @param model The form model describing the structure and properties
     * of the form data included with this request.  The validation procedure
     * may require knowledge of form or parameter properties to complete.
     *
     * @param data The container for all data objects associated with
     * the request.  All parameters specified in the form model are
     * converted to data objects and stored in this container before
     * any form validation procedures are called.
     *
     * @param request The HTTP request information from which the form
     * data was extracted.  Note that the request object is supplied
     * only in case the validation procedure involves contextual
     * information (information extracted from cookies or the peer
     * address, for example).
     *
     * @exception FormProcessException If the data does not pass the
     * check. */

    void validate(FormSectionEvent e) throws FormProcessException;

}
