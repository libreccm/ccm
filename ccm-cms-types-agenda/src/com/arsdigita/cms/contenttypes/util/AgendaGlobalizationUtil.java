/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes.util;

import com.arsdigita.globalization.Globalized;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Compilation of methods to simplify the handling of globalizing keys.
 * Basically it adds the name of package's resource bundle files to the
 * globalize methods and forwards to GlobalizedMessage, shortening the
 * method invocation in the various application classes.
 *
 * @author <a href="mailto:randyg@arsdigita.com">randyg@arsdigita.com</a>
 * @version $Revision: #4 $ $Date: 2004/08/17 $
 */
public class AgendaGlobalizationUtil implements Globalized {
    
    /**  Name of Java resource files to handle Agenda's globalisation.  */
    final public static String BUNDLE_NAME = 
                        "com.arsdigita.cms.contenttypes.AgendaResources";
    
    /**  Name of Java resource files to handle CMS globalisation.  */
    final public static String ALTERNATE_BUNDLE_NAME = 
                        "com.arsdigita.cms.CMSResources";

    /**
     * This returns a globalized message using the type specific bundle,
     * BUNDLE_NAME if the key string contains the modules name agenda, 
     * otherwise the CMS ResourceBundle is used.
     */
    public static GlobalizedMessage globalize(String key) {
        if (key.indexOf(".agenda.") > 0) { 
            return new GlobalizedMessage(key, BUNDLE_NAME);
        } else {
            return new GlobalizedMessage(key, ALTERNATE_BUNDLE_NAME);
        }
    }

    /**
     * Returns a globalized message object, using the package specific bundle,
     * as specified by BUNDLE_NAME. Also takes in an Object[] of arguments to
     * interpolate into the retrieved message using the  MessageFormat class.
     */
    public static GlobalizedMessage globalize(String key, Object[] args) {
        if (key.indexOf(".agenda.") > 0) { 
            return new GlobalizedMessage(key, BUNDLE_NAME, args);
        } else {
            return new GlobalizedMessage(key, ALTERNATE_BUNDLE_NAME, args);
        }
    }
}
