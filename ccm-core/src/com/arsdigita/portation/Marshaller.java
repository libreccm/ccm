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
package com.arsdigita.portation;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central class for exporting and importing objects of this system stored in
 * the database.
 *
 * <info>Exporting or importing object classes need to implement
 * interface identifiable.</info>
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version created on 03.02.2016
 */
public class Marshaller {
    private static final Logger log = Logger.getLogger(Marshaller.class);

    // Assigns lists with objects of the same type as values to their typ as
    // key.
    private Map<Class<? extends Portable>, List<Portable>> classListMap = new HashMap<>();


    /**
     * Main exportUsers method. Organizes the objects into list of the same type
     * and calls a second exportUsers method for each list.
     *
     * @param objects All objects to be exported
     * @param format The exportUsers style/format e.g. CSV or JSON
     * @param filename The name of the file to be exported to
     */
    public void exportObjects(List<? extends Portable> objects, Format format,
                              String filename) {
        putObjects(objects);

        for (Map.Entry<Class<? extends Portable>, List<Portable>>
            classListEntry : classListMap.entrySet()) {

            exportList(classListEntry.getValue(), classListEntry.getKey(),
                    format, filename);
        }
    }

    /**
     * Organizes a list of different {@link Portable} objects into a map
     * assigning lists of the same type to their type as values to a key. The
     * type which all objects of that list have in common is their key.
     * That opens the possibility of being certain of the objects types in
     * the list. Guarantied through this implementation.
     *
     * @param objects list of all objects being organized
     */
    private void putObjects(List<? extends Portable> objects) {
        for (Portable object : objects) {
            Class<? extends Portable> type = object.getClass();

            if (classListMap.containsKey(type)) {
                classListMap.get(type).add(object);
            } else {
                List<Portable> values = new ArrayList<>();
                values.add(object);
                classListMap.put(type, values);
            }
        }
    }

    /**
     * Selects the right marshaller for the given type, initializes that
     * marshaller for the given exportUsers wishes and calls the exportUsers method of
     * that marshaller upon the given list of same typed objects.
     *
     * Naming convention for the exportUsers file name:
     *      <basic file name>__<type/class name>.<format>
     *
     * @param list List of objects to be exported of the same type
     * @param type The class of the type
     * @param format The exportUsers style
     * @param filename The filename
     * @param <P> The type of the current marshaller
     */
    private <P extends Portable> void exportList(List<P> list, Class<?
            extends P> type, Format format, String filename) {
        @SuppressWarnings("unchecked")
        AbstractMarshaller<P> marshaller = (AbstractMarshaller<P>) list.get
                (0).getMarshaller();

        marshaller.prepare(format, filename + "__" + type.toString(),
                false);
        marshaller.exportList(list);
    }

}

