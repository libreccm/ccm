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
package com.arsdigita.templating.html;

import com.arsdigita.util.CheckedWrapperException;

/**
 * Encapsulates a configuration error. Similar to <a
 * href="http://java.sun.com/xml/jaxp/dist/1.1/docs/api/javax/xml/parsers/ParserConfigurationException.html">SAXParserConfigurationError</a>.
 *
 * <p>This should be rewritten not to extend {@link
 * com.arsdigita.util.CheckedWrapperException} if/when we port our stuff to J2SE
 * 1.4. </p>

 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2002-09-09
 * @version $Id: HTMLParserConfigurationException.java 287 2005-02-22 00:29:02Z sskracic $
 **/
public class HTMLParserConfigurationException extends CheckedWrapperException {

    /**
     * Constructor which only takes a message.
     *
     * @param msg a message explaining the cause of this exception
     **/
    public HTMLParserConfigurationException (String msg) {
        super(msg);
    }

    /**
     * Constructor which takes a root cause that this exception will be
     * wrapping.
     **/
    public HTMLParserConfigurationException (Throwable rootCause) {
        super(rootCause);
    }

    /**
     * Constructor which takes a message string and a root cause that this
     * exception will be wrapping.  The message string should be something
     * different than {@link java.lang.Throwable#getMessage()
     * rootCause.getMessage()} would normally provide.
     */
    public HTMLParserConfigurationException (String s, Throwable rootCause) {
        super(s, rootCause);
    }
}
