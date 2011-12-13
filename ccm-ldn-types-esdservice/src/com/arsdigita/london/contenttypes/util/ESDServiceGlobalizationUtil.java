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
 */
package com.arsdigita.london.contenttypes.util;

import com.arsdigita.globalization.GlobalizedMessage;

/**
 * <p>
 * Contains methods to simplify globalizing keys related to 
 * <code>ESDService</code> content type object.
 * </p>

 * @author Shashin Shinde <a href="mailto:sshinde@redhat.com">sshinde@redhat.com</a>
 *
 * @version $Id: ESDServiceGlobalizationUtil.java 287 2005-02-22 00:29:02Z sskracic $
 * 
 */
public class ESDServiceGlobalizationUtil {

  final public static String BUNDLE_NAME =
    "com.arsdigita.london.contenttypes.util.ESDServiceResourceBundle";

  /**
   *  This returns a globalized message using the type specific bundle,
   *  BUNDLE_NAME
   */
  public static GlobalizedMessage globalize(String key) {
    return new GlobalizedMessage(key, BUNDLE_NAME);
  }

  /**
   *  This returns a globalized message using the type specific bundle,
   *  BUNDLE_NAME
   */
  public static GlobalizedMessage globalize(String key, Object[] args) {
    return new GlobalizedMessage(key, BUNDLE_NAME, args);
  }

}
