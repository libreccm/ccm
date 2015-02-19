/*
 * Copyright (C) 2013 University of Bremen. All Rights Reserved.
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

package com.arsdigita.camden.cms.contenttypes.util;

import com.arsdigita.globalization.Globalized;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Compilation of methods to simplify the handling of globalizing keys.
 * Basically it adds the name of package's resource bundle files to the
 * globalize methods and forwards to GlobalizedMessage, shortening the
 * method invocation in the various application classes.
 * 
 * @author pb
 */
public class EFormGlobalizedMsg implements Globalized {
    
    /**  Name of Java resource files to handle CMS's globalisation.  */
    final public static String BUNDLE_NAME = 
        "com.arsdigita.camden.cms.contenttypes.EFormResources";
    
    /**
     * This returns a localized globalized message for the NAME entry field
     * using the type specific bundle,  BUNDLE_NAME
     */
    public static GlobalizedMessage getTitle() {
        return new GlobalizedMessage(
                "camden.cms.contenttypes.eform.title", 
                BUNDLE_NAME);
    }
    
    /**
     * This returns a localized globalized message for the NAME entry field
     * using the type specific bundle,  BUNDLE_NAME
     */
    public static GlobalizedMessage getLocation() {
        return new GlobalizedMessage(
                "camden.cms.contenttypes.eform.location", 
                BUNDLE_NAME);
    }
    
    /**
     * This returns a localized globalized message for the NAME entry field
     * using the type specific bundle,  BUNDLE_NAME
     */
    public static GlobalizedMessage getDescription() {
        return new GlobalizedMessage(
                "camden.cms.contenttypes.eform.description", 
                BUNDLE_NAME);
    }
    
    /**
     * This returns a localized globalized message for the NAME entry field
     * using the type specific bundle,  BUNDLE_NAME
     */
    public static GlobalizedMessage getName() {
        return new GlobalizedMessage(
                "camden.cms.contenttypes.eform.name", 
                BUNDLE_NAME);
    }
    
    /**
     * This returns a localized globalized message based on its key and using
     * the type specific bundle,  BUNDLE_NAME
     */
    public static GlobalizedMessage get(String key) {

        return new GlobalizedMessage(key, BUNDLE_NAME);

    }

    
}
