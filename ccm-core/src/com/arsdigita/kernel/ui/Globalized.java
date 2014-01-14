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
package com.arsdigita.kernel.ui;

/**
 * 
 * Standard, final constants used by the globalization APIs. This
 * interface is designed to be extended on a package-by-package basis
 * to include package-specific constants. A typical package specific
 * constant would be the resource bundle name for a given package. By
 * extending the Globalized interface and defining a final static
 * BUNDLE_NAME constant, classes implementing the Globalized interface
 * could call {@link com.arsdigita.globalization.GlobalizedMessage}
 * and pass in the BUNDLE_NAME constant i.e.,
 *
 * <p>
 * <pre>
 *  new GlobalizedMessage("forums.newpost.proofread",
 *                        BUNDLE_NAME)
 * </pre>
 * </p>
 *
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 */
public interface Globalized extends com.arsdigita.globalization.Globalized {

    public static final String BUNDLE_NAME = "com.arsdigita.kernel/ui/KernelResources";

}
