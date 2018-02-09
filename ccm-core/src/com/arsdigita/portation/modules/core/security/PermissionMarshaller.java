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
public class PermissionMarshaller extends AbstractMarshaller<Permission> {
    private static PermissionMarshaller instance;

    static {
        instance = new PermissionMarshaller();
    }

    /**
     * Getter for instance of this singleton.
     *
     * @return instance of the singleton
     */
    public static PermissionMarshaller getInstance() {
        return instance;
    }

    /**
     * Passes the parameters for the file to which the {@link Permission}-
     * objects will be exported to down to its corresponding
     * {@link AbstractMarshaller<Permission>} and then requests this
     * {@link AbstractMarshaller<Permission>} to start the export of all its
     * {@link Permission}s.
     *
     * @param format The format of the file to which will be exported to
     * @param pathName The name for the file
     * @param indentation Whether to use indentation in the file
     */
    @Override
    public void marshallAll(final Format format,
                            final String pathName,
                            final boolean indentation) {
        System.out.print("\tExporting permissions...");
        prepare(format, pathName, "permissions", indentation);
        exportList(new ArrayList<>(NgCoreCollection.permissions.values()));
        System.out.print("\tdone.\n");
    }
}
