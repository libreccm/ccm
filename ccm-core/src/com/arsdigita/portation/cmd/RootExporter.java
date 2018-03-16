/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.portation.cmd;

import com.arsdigita.portation.modules.CoreExporter;

import java.lang.reflect.Method;

/**
 * Helper class to bundle all export calls.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 2/7/18
 */
public class RootExporter {

    /**
     * Root method to call all exportation
     *
     * @throws Exception if classes outwards of core will not be found
     */
    @SuppressWarnings("unchecked")
    public static void rootExportExecution() throws Exception {
        // Core
        CoreExporter.getInstance().startMarshaller();

        // Ldn-Terms
        /*Class cls = Class
                .forName("com.arsdigita.london.terms.portation.modules" +
                        ".LdnTermsExporter");
        if (cls != null) {
            Method startExport = cls
                    .getDeclaredMethod("startMarshaller");
            startExport.invoke(cls.newInstance());
        }*/

        // ...
    }
}
