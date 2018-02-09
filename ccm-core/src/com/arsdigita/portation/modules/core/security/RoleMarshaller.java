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
package com.arsdigita.portation.modules.core.security;

import com.arsdigita.portation.AbstractMarshaller;
import com.arsdigita.portation.Format;
import com.arsdigita.portation.conversion.NgCoreCollection;

import java.util.ArrayList;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created on 6/15/16
 */
public class RoleMarshaller extends AbstractMarshaller<Role> {
    private static RoleMarshaller instance;

    static {
        instance = new RoleMarshaller();
    }

    /**
     * Getter for the instance of the singleton.
     *
     * @return instance of this singleton
     */
    public static RoleMarshaller getInstance() {
        return instance;
    }

    /**
     * Passes the parameters for the file to which the {@link Role}-objects
     * will be exported to down to its corresponding
     * {@link AbstractMarshaller<Role>} and then requests this
     * {@link AbstractMarshaller<Role>} to start the export of all its
     * {@link Role}s.
     *
     * @param format The format of the file to which will be exported to
     * @param pathName The name for the file
     * @param indentation Whether to use indentation in the file
     */
    @Override
    public void marshallAll(final Format format,
                            final String pathName,
                            final boolean indentation) {
        System.out.print("\tExporting roles...");
        prepare(format, pathName, "roles", indentation);
        exportList(new ArrayList<>(NgCoreCollection.roles.values()));
        System.out.print("\t\tdone.\n");
    }
}
