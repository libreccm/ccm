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
package com.arsdigita.portation.modules.core.core;

import com.arsdigita.portation.AbstractMarshaller;
import com.arsdigita.portation.Format;
import com.arsdigita.portation.cmd.ExportLogger;
import com.arsdigita.portation.conversion.NgCoreCollection;

import java.util.ArrayList;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 8/3/17
 */
public class ResourceTypeMarshaller extends AbstractMarshaller<ResourceType> {
    private static ResourceTypeMarshaller instance;

    static {
        instance = new ResourceTypeMarshaller();
    }

    /**
     * Getter for the instance of this singleton.
     *
     * @return instance of the singleton
     */
    public static ResourceTypeMarshaller getInstance() {
        return instance;
    }

    /**
     * Passes the parameters for the file to which the {@link ResourceType}-
     * objects will be exported to down to its corresponding
     * {@link AbstractMarshaller<ResourceType>} and then requests this
     * {@link AbstractMarshaller<ResourceType>} to start the export of all
     * its {@link ResourceType}s.
     *
     * @param format The format of the file to which will be exported to
     * @param pathName The name for the file
     * @param indentation Whether to use indentation in the file
     */
    @Override
    public void marshallAll(final Format format,
                            final String pathName,
                            final boolean indentation) {
        ExportLogger.exporting("resource types");
        prepare(format, pathName, "resourceTypes", indentation);
        exportList(new ArrayList<>(NgCoreCollection.resourceTypes.values()));
    }
}
