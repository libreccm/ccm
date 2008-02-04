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
package com.arsdigita.initializer;

import java.util.ArrayList;
import java.util.List;

/**
 * A test Initializer
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #7 $ $Date: 2004/08/16 $
 */

public class FooInitializer implements Initializer {

    public final static String versionId = "$Id: FooInitializer.java 741 2005-09-02 10:21:19Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static boolean isStarted = false;

    public static boolean isStarted() {
        return isStarted;
    }

    Configuration m_config = new Configuration();

    public FooInitializer() throws InitializationException {
        m_config.initParameter("stringParam", "This is a usage string.",
                               String.class,"This is a string.");
        m_config.initParameter("objectParam", "A dummy object.", Object.class);
        m_config.initParameter("listParam", "Should be a list.",
                               java.util.List.class, new ArrayList());
    }

    public Configuration getConfiguration() {
        return m_config;
    }

    public void startup() {
        isStarted = true;
    }

    public void shutdown() {
        isStarted = false;
    }

}
