/*
 * Copyright (C) 2013 Peter Boy, University of Bremen. All Rights Reserved.
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

package com.arsdigita.cms.contentassets.util;

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
public class ImageStepGlobalizationUtil implements Globalized {
    
    /**  Name of Java resource files to handle RelatedLink's globalisation.  */
    final public static String BUNDLE_NAME = 
                        "com.arsdigita.cms.contentassets.ImageStepResources";
    
    /**  Name of Java resource files to handle CMS globalisation.  */
    final public static String ALTERNATE_BUNDLE_NAME = 
                        "com.arsdigita.cms.CMSResources";

    /**
     * Returns a globalized message using the appropriate bundle.
     * If the key string contains the modules name image_step the package
     * specific bundle is used, otherwise the CMS ResourceBundle.
     */
    public static GlobalizedMessage globalize(String key) {
        if (key.indexOf(".image_step.") > 0) { 
            return new GlobalizedMessage(key, BUNDLE_NAME);
        } else {
            return new GlobalizedMessage(key, ALTERNATE_BUNDLE_NAME);
        }
    }

    /**
     * Returns a globalized message object, using the approprate bundle,
     * takeing in an Object[] of arguments to interpolate into the retrieved 
     * message using the  MessageFormat class.
     * If the key string contains the modules name image_Step the package 
     * specific bundle is used, otherwise the CMS ResourceBundle.
     */
    public static GlobalizedMessage globalize(String key, Object[] args) {
        if (key.indexOf(".image_step.") > 0) { 
            return new GlobalizedMessage(key, BUNDLE_NAME, args);
        } else {
            return new GlobalizedMessage(key, ALTERNATE_BUNDLE_NAME, args);
        }
    }

}
