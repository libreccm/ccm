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
package com.arsdigita.globalization;
// import java.text.DateFormat;

/**
 * Standard, final constants used by the globalization APIs. This
 * interface is designed to be extended on a package-by-package basis
 * to include package-specific constants. A typical package specific
 * constant would be the resource bundle name for a given package. By
 * extending the Globalized interface and defining a final static
 * BUNDLE_NAME constant, classes implementing the Globalized interface
 * could call {@link GlobalizedMessage} and pass in the BUNDLE_NAME
 * constant i.e.,
 *
 * <p>
 * <pre>
 *  new GlobalizedMessage("forums.newpost.proofread",
 *                        BUNDLE_NAME)
 * </pre>
 * </p>
 *
 * @version $Revision: #9 $ $Date: 2004/08/16 $
 * @version $Id: Globalized.java 287 2005-02-22 00:29:02Z sskracic $
 */
public interface Globalized {

    /** The default format for displaying dates.                             */
    public final static int DATE_DISPLAY_FORMAT = java.text.DateFormat.MEDIUM;

    /** The default format for displaying time.                              */
    public final static int TIME_DISPLAY_FORMAT = java.text.DateFormat.SHORT;

    /** Override the value of this string for your particular package.       */
    public final static String BUNDLE_NAME = "com.arsdigita.globalization";

}
